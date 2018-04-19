/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.edu.sutd.bank.webapp.servlet;

import static sg.edu.sutd.bank.webapp.servlet.ServletPaths.UPLOAD;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.lang.IllegalStateException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.ClientAccount;
import sg.edu.sutd.bank.webapp.model.ClientTransaction;
import sg.edu.sutd.bank.webapp.model.TransactionStatus;
import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAO;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAOImpl;
import sg.edu.sutd.bank.webapp.service.ClientTransactionDAO;
import sg.edu.sutd.bank.webapp.service.ClientTransactionDAOImpl;
import sg.edu.sutd.bank.webapp.service.TransactionCodesDAO;
import sg.edu.sutd.bank.webapp.service.TransactionCodesDAOImp;


@WebServlet(UPLOAD)
@MultipartConfig
public class UploadServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private final ClientTransactionDAO clientTransactionDAO = new ClientTransactionDAOImpl();
    private final ClientAccountDAO clientAccountDAO = new ClientAccountDAOImpl();
    private final TransactionCodesDAO transactionCodesDAO = new TransactionCodesDAOImp();
    public static final BigDecimal TRANSFER_LIMIT = new BigDecimal(10);
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, FileNotFoundException {
        File uploaded = File.createTempFile("test", ".csv");
        Part filepart = req.getPart("uploadFile");
        InputStream fileContent = filepart.getInputStream();
        byte[] buffer = new byte[fileContent.available()];
        fileContent.read(buffer);

        OutputStream out = new FileOutputStream(uploaded);
        out.write(buffer);
        
        
        
        
        //Sanitize upload
        //Files are not kept on disk and are only read and data extracted
        //Only correct transaction codes are permitted thus anything else in code section will be discarded
        //Only Integer is accepted for Account, thus all else caught by the NumberFormatException
        //Only BigDecimal is accepted for Amount, thus all else is caught by the NumberFormatException
        //Any other file type, size or malicious file uploaded is caught by IllegalStateException
        
        try{
           CsvReader csv = new CsvReader(new FileReader(uploaded)); //
           System.out.println(csv.getRawRecord());
           csv.readHeaders();
            while((csv.readRecord()))
            {
            	
                try{               	
                	
                    User user = new User(getUserId(req));
                    ClientAccount account = clientAccountDAO.load(user);
                    ClientTransaction transaction = new ClientTransaction();
                    transaction.setUser(user);
                    transaction.setTransCode(csv.get("Code"));
                    transaction.setToAccountNum(Integer.parseInt(csv.get("Account")));
                    transaction.setAmount(new BigDecimal(csv.get("Amount")));
                    if(account.getAmount().compareTo(transaction.getAmount()) < 0) {
                        throw new SQLException("No enough money");
                    } else if (!transactionCodesDAO.check(transaction.getTransCode(), transaction.getUser().getId())) {
                        throw new SQLException("Transaction code is not valid");
                    } else {
                        if(transaction.getAmount().compareTo(TRANSFER_LIMIT) < 0) {
                            transaction.setStatus(TransactionStatus.APPROVED);
                            clientTransactionDAO.updateAccount(transaction);
                        } else {
                            transaction.setStatus(TransactionStatus.PENDING);
                        }
                        account.setAmount(account.getAmount().subtract(transaction.getAmount()));
                    }
                    clientAccountDAO.update(account);
                    clientTransactionDAO.create(transaction);

                }catch (ServiceException | SQLException e) {
                    sendError(req, e.getMessage()); //
                    forward(req, resp);
                }catch (IllegalStateException | NumberFormatException e) {
                    sendError(req, "There's an issue with your file!");
                    forward(req, resp);
                }
                
            }
            
            uploaded.deleteOnExit();
            csv.close();
            redirect(resp, ServletPaths.CLIENT_DASHBOARD_PAGE);
        }catch (FileNotFoundException ex){

        } finally {
        	uploaded.delete();
            out.close();
            
        }
        
        
    }
}