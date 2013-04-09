package com.javalabs.services;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import com.javalabs.DAO.PersonDAO;
import com.javalabs.handler.Handler;
import com.javalabs.handler.IAction;
import com.javalabs.model.PersonBean;

public class PersonService implements IAction {

	private HttpServletRequest request = null;
	private PersonBean person = null;
	private PersonDAO personDAO = null;
	private Handler handler = null;
	
	public PersonService() {
		super();
	}
	
	public PersonService(HttpServletRequest req) {
		super();
		this.request = req;
		personDAO = new PersonDAO();
		handler = new Handler(req);
	}
	
	public void setRequest(HttpServletRequest req) {
		this.request = req;
		handler.setRequest(req);
	}

	/**
	 * 
	 * @return
	 */
	private Integer newPerson(){
		
		Integer result = 0;

		request.setAttribute("title", "New person");
		request.setAttribute("formAction", "person/add");
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	private Integer addPerson(){
		
		Integer result = 0;

		if (handler.getMethod().equalsIgnoreCase("post")) {	/* Create new person */
			try {
				personDAO.insert(populate());
				request.setAttribute("msg", "Person added successfully!");
				result = 0;
			} catch (SQLException e) {
				request.setAttribute("err", e.getMessage());
				e.printStackTrace();
				result = -1;
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	private Integer lstPersons() {
	
		Integer result = 0;
		
		try {
			request.setAttribute("persons", personDAO.getAllDetails());
			result = 0;
		} catch (SQLException e) {
			request.setAttribute("err", e.getMessage());
			e.printStackTrace();
			result = -1;
		}
		
		return result;
		
	}

	/**
	 * 
	 * @return
	 */
	private Integer  edtPerson(){
		Integer result = 0;
		
		if (handler.getMethod().equalsIgnoreCase("post")) {
			person = populate();
			request.setAttribute("person", person);
			request.setAttribute("msg", "Person updated successfully!");
			request.setAttribute("formAction", "person/upd");
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	private Integer  updPerson(){
		Integer result = 0;
		
		if (handler.getMethod().equalsIgnoreCase("post")) {
			
			try {
				person = personDAO.update(populate());
				request.setAttribute("person", person);
				request.setAttribute("msg", "Person updated successfully!");
				result = lstPersons();
			} catch (SQLException e) {
				request.setAttribute("err", e.getMessage());
				//person = populate();
				//person.setId(Long.valueOf(request.getParameter("id")));
				//request.setAttribute("person", person);
				e.printStackTrace();
				result = -1;
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @return id of the person deleted 
	 * 		   -1 if an error occur
	 */
	private Integer delPerson(){
		Integer result = 0;
		
		if (handler.getId() != 0){
			try {
				personDAO.delete(new PersonBean(handler.getId()));
				request.setAttribute("msg", "Person deleted successfully!");
			} catch (SQLException e) {
				request.setAttribute("err", e.getMessage());
				e.printStackTrace();
				result = -1;
			}
		} else {
			request.setAttribute("msg", "None person was deleted.");
		}
		
		return result;
	}
	
	/**
	 * Load parameters received from the form into a person object
	 * or pathInfo
	 * 
	 * @return
	 */
	private PersonBean populate(){
		person = new PersonBean();

		String id = request.getParameter("id");
		
		if (id == null) { /* New person */
			person.setFirstName(request.getParameter("firstName"));
			person.setLastName(request.getParameter("lastName"));
			java.util.Date date = new java.util.Date();
			SimpleDateFormat sDate = new SimpleDateFormat("dd/MM/yyyy");
			
			try {
				date = sDate.parse(request.getParameter("dob"));
				person.setDob(new Date(date.getTime()));
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
		} else { /* Populate data from an already created person */
			person.setId(handler.getId());
			
			try {
				person = new PersonDAO().select(person);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return person;
	}
	
	@Override
	public String execute() {
		
		String page;
		String entity = handler.getEntity();
		String action = handler.getAction();
		Integer result = -1;
		
		if (entity.equals("person")) {
			if (action.equals("lst")) {
				result = this.lstPersons();
			} else if (action.equals("new")) {
				result = this.newPerson();
			} else if (action.equals("add")) {
				result = this.addPerson();
			} else if (action.equals("edt")) {
				result = this.edtPerson();
			} else if (action.equals("upd")) {
				result = this.updPerson();
			} else if (action.equals("del")) {
				result = this.delPerson();
			}
		}
		
		if (result == 0) {
			page = handler.getPathOK(); 
		} else {
			page = handler.getPathKO();
		}
System.out.println(page);
		return page;
	}

    /**
     * This function checks if the string passed as an arg 
     * is a number.
     * @param num
     * @return boolean
     */
    private boolean isNumeric(String num){
            try {
                    Integer.parseInt(num);          
                    return true;
            } catch (NumberFormatException nfe) {
                    System.out.println("It is not numeric." + num);
                    nfe.printStackTrace();
                    return false;
            }
    }
}