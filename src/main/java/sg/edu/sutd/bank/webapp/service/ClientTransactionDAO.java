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

package sg.edu.sutd.bank.webapp.service;

import java.util.List;

import sg.edu.sutd.bank.webapp.commons.ServiceException;
import sg.edu.sutd.bank.webapp.model.ClientTransaction;
import sg.edu.sutd.bank.webapp.model.User;


public interface ClientTransactionDAO {

	void create(ClientTransaction clientTransaction) throws ServiceException;
	
	void updateAccount(ClientTransaction clientTransaction) throws ServiceException; //added by me

	List<ClientTransaction> load(User user) throws ServiceException;
	
	List<ClientTransaction> loadWaitingList() throws ServiceException;

	void updateDecision(ClientTransaction transaction) throws ServiceException;
	
    ClientTransaction select(int transId) throws ServiceException;
}
