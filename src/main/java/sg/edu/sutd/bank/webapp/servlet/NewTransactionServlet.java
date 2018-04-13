/*
 * Copyright 2017 SUTD Licensed under the
	Educational Community License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may
	obtain a copy of the License at

https://opensource.org/licenses/ECL-2.0

	Unless required by applicable law or agreed to in writing,
	software distributed under the License is distributed on an "AS IS"
	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
	or implied. See the License for the specific language governing
	permissions and limitations under the License.
 */

package sg.edu.sutd.bank.webapp.servlet;

import static sg.edu.sutd.bank.webapp.servlet.ServletPaths.NEW_TRANSACTION;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.lang.NumberFormatException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.ClientTransaction;
import sg.edu.sutd.bank.webapp.model.TransactionStatus;
import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.model.ClientAccount;
import sg.edu.sutd.bank.webapp.service.ClientTransactionDAO;
import sg.edu.sutd.bank.webapp.service.ClientTransactionDAOImpl;
import sg.edu.sutd.bank.webapp.service.TransactionCodesDAO;
import sg.edu.sutd.bank.webapp.service.TransactionCodesDAOImp;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAO;
import sg.edu.sutd.bank.webapp.service.ClientAccountDAOImpl;

@WebServlet(NEW_TRANSACTION)
public class NewTransactionServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private ClientTransactionDAO clientTransactionDAO = new ClientTransactionDAOImpl();
	private final ClientAccountDAO clientAccountDAO = new ClientAccountDAOImpl();
    private final TransactionCodesDAO transactionCodesDAO = new TransactionCodesDAOImp();
    public static final BigDecimal TRANSFER_LIMIT = new BigDecimal(10);
    
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			ClientTransaction clientTransaction = new ClientTransaction();
			User user = new User(getUserId(req));
			clientTransaction.setUser(user);
			clientTransaction.setAmount(new BigDecimal(req.getParameter("amount")));
			clientTransaction.setTransCode(req.getParameter("transcode"));
			clientTransaction.setToAccountNum(Integer.parseInt(req.getParameter("toAccountNum")));
			
			ClientAccount clientAccount = clientAccountDAO.load(user);

            if(clientAccount.getAmount().compareTo(clientTransaction.getAmount()) < 0) {
                throw new SQLException("Your balance is not enough!");
            } else if (!transactionCodesDAO.check(clientTransaction.getTransCode(), clientTransaction.getUser().getId())) {
                throw new SQLException("Your transaction code is invalid!");
            } else {
                if(clientTransaction.getAmount().compareTo(TRANSFER_LIMIT) < 0) {
                    clientTransaction.setStatus(TransactionStatus.APPROVED);
                    clientTransactionDAO.updateAccount(clientTransaction);
                } else {
                    clientTransaction.setStatus(TransactionStatus.PENDING);
                }
                clientAccount.setAmount(clientAccount.getAmount().subtract(clientTransaction.getAmount()));
            }
			
			clientAccountDAO.update(clientAccount);
			clientTransactionDAO.create(clientTransaction);
			redirect(resp, ServletPaths.CLIENT_DASHBOARD_PAGE);
		} catch (ServiceException | SQLException e) {
            sendError(req, e.getMessage());
            forward(req, resp);
        }
	}
}
