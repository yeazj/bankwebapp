/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.edu.sutd.bank.webapp.servlet;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import sg.edu.sutd.bank.webapp.commons.Constants;
import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.ClientAccount;
import sg.edu.sutd.bank.webapp.model.ClientTransaction;
import sg.edu.sutd.bank.webapp.model.TransactionStatus;
import sg.edu.sutd.bank.webapp.model.User;
import static sg.edu.sutd.bank.webapp.service.AbstractDAOImpl.connectDB;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAO;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAOImpl;
import sg.edu.sutd.bank.webapp.service.ClientTransactionDAO;
import sg.edu.sutd.bank.webapp.service.ClientTransactionDAOImpl;
import sg.edu.sutd.bank.webapp.service.TransactionCodesDAO;
import sg.edu.sutd.bank.webapp.service.TransactionCodesDAOImp;
import static sg.edu.sutd.bank.webapp.servlet.ServletPaths.UPLOAD;


@WebServlet(UPLOAD)
@MultipartConfig
public class UploadServlet extends DefaultServlet {
    private final ClientTransactionDAO clientTransactionDAO = new ClientTransactionDAOImpl();
    private final ClientAccountDAO clientAccountDAO = new ClientAccountDAOImpl();
    private final TransactionCodesDAO transactionCodesDAO = new TransactionCodesDAOImp();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, FileNotFoundException {
        Connection conn = null;
        File tmpFile = File.createTempFile("test", ".csv");
        Part filepart = req.getPart("uploadFile");
        InputStream fileContent = filepart.getInputStream();
        byte[] buffer = new byte[fileContent.available()];
        fileContent.read(buffer);

        OutputStream out = new FileOutputStream(tmpFile);
        out.write(buffer);
        try{
            CSVReader csv = new CSVReader(new FileReader(tmpFile));
            String[] val;
            while((val = csv.readNext()) != null)
            {
                try{
                    conn = connectDB();
                    conn.setAutoCommit(false);

                    User user = new User(getUserId(req));
                    ClientAccount account = clientAccountDAO.loadAccount(user);
                    ClientTransaction transaction = new ClientTransaction();
                    transaction.setUser(user);
                    transaction.setTransCode(val[0]);
                    transaction.setToAccountNum(Integer.parseInt(val[1]));
                    transaction.setAmount(new BigDecimal(val[2]));
                    if(account.getAmount().compareTo(transaction.getAmount()) < 0) {
                        throw new SQLException("No enough money");
                    } else if (!transactionCodesDAO.validateTransactionCode(transaction.getTransCode(), transaction.getUser().getId(), conn)) {
                        throw new SQLException("Transaction code is not valid");
                    } else {
                        if(transaction.getAmount().compareTo(Constants.TRANSACTION_LIMIT) < 0) {
                            transaction.setStatus(TransactionStatus.APPROVED);
                            clientAccountDAO.executeTransaction(transaction, conn);
                        } else {
                            transaction.setStatus(TransactionStatus.WAITING);
                        }
                        account.setAmount(account.getAmount().subtract(transaction.getAmount()));
                    }
                    clientAccountDAO.update(account, conn);
                    clientTransactionDAO.create(transaction, conn);

                    conn.commit();
                }catch (ServiceException | SQLException e) {
                    try {conn.rollback();} catch(SQLException ex) {}
                    sendError(req, e.getMessage());
                    forward(req, resp);
                } finally {
                    try {
                        if(conn != null && !conn.isClosed()) {
                            conn.close();
                        }
                    } catch (SQLException ex) {}
                }
            }
            
            tmpFile.deleteOnExit();
            redirect(resp, ServletPaths.CLIENT_DASHBOARD_PAGE);
        }catch (FileNotFoundException ex){

        } finally {
            tmpFile.delete();
        }
        
        
    }
}
