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

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static sg.edu.sutd.bank.webapp.servlet.ServletPaths.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.User;
import sg.edu.sutd.bank.webapp.model.UserStatus;
import sg.edu.sutd.bank.webapp.service.UserDAO;
import sg.edu.sutd.bank.webapp.service.UserDAOImpl;


@WebServlet(LOGIN)
public class LoginServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private UserDAO userDAO = new UserDAOImpl();
	private MessageDigest messageDigest;
	private String passwordhash;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String userName = req.getParameter("username");
			String password = req.getParameter("password");
			User user = userDAO.loadUser(userName);
			
			//Sanitize login
			//Password : Minimum 6 characters, at least one letter, one number and one special character
			if (!userName.matches("[\\w*]*") || !password.matches("[\\w+@$!%*#?&]*")) {
				sendError(req, "Invalid characters are not allowed in form");
				forward(req, resp);
			}
			
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(password.getBytes());
			byte byteData[] = messageDigest.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<byteData.length;i++) 
			{
				String hex=Integer.toHexString(0xff & byteData[i]);
				if(hex.length()==1) 
				{
					hexString.append('0');
				}
				hexString.append(hex);
			}
			passwordhash = hexString.toString();
			
			if (user != null && (user.getStatus() == UserStatus.APPROVED)) {
				req.login(userName, passwordhash);
				HttpSession session = req.getSession(true);
				session.setAttribute("authenticatedUser", req.getRemoteUser());
				setUserId(req, user.getId());
				if (req.isUserInRole("client")) {
					redirect(resp, CLIENT_DASHBOARD_PAGE);
				} else if (req.isUserInRole("staff")) {
					redirect(resp, STAFF_DASHBOARD_PAGE);
				}
				return;
			}
			sendError(req, "Invalid username/password!");
		} catch(ServletException | ServiceException | NoSuchAlgorithmException ex) {
			sendError(req, ex.getMessage());
		}
		forward(req, resp);
	}

}
