package co.promethean2k18.com.Data_;

public class Statistics_model {
    String downloads,total_registrations,payments,special_event_registrations,dept_event_registrations,payments_done,amount_recieved_through_app_payments;


    public String getAmount_recieved_through_app_payments() {
        return amount_recieved_through_app_payments;
    }

    public void setAmount_recieved_through_app_payments(String amount_recieved_through_app_payments) {
        this.amount_recieved_through_app_payments = amount_recieved_through_app_payments;
    }

    public String getDownloads() {

        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getTotal_registrations() {
        return total_registrations;
    }

    public void setTotal_registrations(String total_registrations) {
        this.total_registrations = total_registrations;
    }

    public String getPayments() {
        return payments;
    }

    public void setPayments(String payments) {
        this.payments = payments;
    }

    public String getSpecial_event_registrations() {
        return special_event_registrations;
    }

    public void setSpecial_event_registrations(String special_event_registrations) {
        this.special_event_registrations = special_event_registrations;
    }

    public String getDept_event_registrations() {
        return dept_event_registrations;
    }

    public void setDept_event_registrations(String dept_event_registrations) {
        this.dept_event_registrations = dept_event_registrations;
    }

    public String getPayments_done() {
        return payments_done;
    }

    public void setPayments_done(String payments_done) {
        this.payments_done = payments_done;
    }
}
