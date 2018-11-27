package com.ruralhousejsf.model.availability;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import businessLogic.AppFacade;
import businessLogic.ApplicationFacadeInterface;
import configuration.Config;
import configuration.ConfigXML;
import domain.Offer;
import domain.Review.ReviewState;
import domain.RuralHouse;
import exceptions.BadDatesException;

@ManagedBean(name="queryAvailability")
@SessionScoped
public class QueryAvailabilityBean {

	private int nights;
	private Date startDate;
	private Date endDate;

	private RuralHouse ruralHouse;
	private List<RuralHouse> ruralHouseList;

	private List<Offer> offers;

	private ApplicationFacadeInterface applicationFacade;

	public QueryAvailabilityBean() {

		Config config = ConfigXML.loadConfig(AppFacade.class.getResource("db/config.xml").getFile());
		applicationFacade = AppFacade.loadConfig(config);
		ruralHouseList = applicationFacade.getRuralHouses(ReviewState.APPROVED);

//		ruralHouses = new LinkedHashMap<String, RuralHouse>();
//		for (RuralHouse ruralHouse : ruralHouseList) {
//			ruralHouses.put(ruralHouse.getId() + " : " + ruralHouse.getName(), ruralHouse);
//		}

	}

	public List<RuralHouse> getRuralHouseList() {
		return ruralHouseList;
	}

	public void setRuralHouses(List<RuralHouse> ruralHouseList) {
		this.ruralHouseList = ruralHouseList;
	}

	public RuralHouse getRuralHouse() {
		return ruralHouse;
	}

	public void setRuralHouse(RuralHouse ruralHouse) {
		this.ruralHouse = ruralHouse;
	}

	public int getNights() {
		return nights;
	}

	public void setNights(int nights) {
		this.nights = nights;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<Offer> getOfferList() {
		return offers;
	}

	public void setOfferList(List<Offer> offerList) {
		this.offers = offerList;
	}
	
	private Date addDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(date); 
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	public void dynamicRender(AjaxBehaviorEvent event) {

		FacesContext context = FacesContext.getCurrentInstance();

		if(!context.isValidationFailed()) {
			
			endDate = addDays(startDate, nights);
			
			try {
				offers =  applicationFacade.getOffers(ruralHouse, getStartDate(), getEndDate());
			} catch (BadDatesException e) {
				e.printStackTrace();
				UIComponent target = event.getComponent().findComponent("queryAvailability:msg");
				context.addMessage(target.getId(), createMessage(FacesMessage.SEVERITY_INFO, "Bad Dates Exception", e.getMessage()));
				context.validationFailed();
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("Nights: " + nights + System.lineSeparator());
			sb.append("StartDate: " + startDate + System.lineSeparator());
			sb.append("EndDate: " + endDate + System.lineSeparator());
			sb.append("RuralHouseLabel: " + ruralHouse + System.lineSeparator());
			sb.append("RuralHouses: " + ruralHouseList + System.lineSeparator());
			sb.append("Offers: " + offers + System.lineSeparator());
			System.out.println(sb.toString());

			// TODO: Show offers

		}

	}

	private FacesMessage createMessage(FacesMessage.Severity severity, String summary, String content) {
		return new FacesMessage(severity, summary, content);
	}

}
