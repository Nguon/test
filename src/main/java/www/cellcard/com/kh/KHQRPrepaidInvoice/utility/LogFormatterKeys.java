package www.cellcard.com.kh.KHQRPrepaidInvoice.utility;

public enum LogFormatterKeys {

    service_name("service_name"),
    account_id("account_id"),
    source_account_id("source_account_id"),
    target_account_id("target_account_id"),
    method_name("method_name"),
    request_plan("request_plan"),
    old_plan("old_plan"),
    purchase_fee("purchase_fee"),
    sale_id("sale_id"),
    channel("channel"),
    request_id("request_id"),
    transaction_id("transaction_id"),
    uuid("uuid"),
    tariff_plan_cosp_id("tariff_plan_cosp_id"),
    class_of_service_id("class_of_service_id"),
    api("api"),
    step("step"),
    action("action"),
    result("result"),
    error_code("error_code"),
    error_message("error_message"),
    nei("nei"),
    server_host("server_host"),
    client_ip("client_ip"),
    username("username"),
    transaction_time("transaction_time");
  
    private String key;
    LogFormatterKeys(String key) {
        this.key = key;
    }
  
    public String getKey() {
        return key;
    }
  }