package www.cellcard.com.kh.KHQRPrepaidInvoice.utility;

import java.util.HashMap;

public class LogFormatter {
    private String logMessageTmp = "";
  private String logMessage ="service_name = , "
                             .concat("account_id = , ")
                             .concat("source_account_id = , ")
                             .concat("target_account_id = , ")
                             .concat("method_name = , ")
                             .concat("request_plan = , ")
                             .concat("old_plan = , ")
                             .concat("purchase_fee = , ")
                             .concat("sale_id = , ")
                             .concat("channel = , ")
                             .concat("request_id = , ")
                             .concat("transaction_id = , ")
                             .concat("uuid = , ")
                             .concat("tariff_plan_cosp_id = , ")
                             .concat("class_of_service_id = , ")
                             .concat("api = , ")
                             .concat("step = , ")
                             .concat("action = , ")
                             .concat("result = , ")
                             .concat("error_code = , ")
                             .concat("error_message = , ")
                             .concat("nei = , ")
                             .concat("server_host = , ")
                             .concat("client_ip = , ")
                             .concat("username = ,")
                             .concat("transaction_time = ");
  
  public String getLogMessage(HashMap<String,String> hmLog ) {
      this.logMessageTmp = this.logMessage;
      try {
        hmLog.forEach((key,value)->{
          if (value != null) {
              value = value.replaceAll("\\$", "#").replaceAll(",", "#");
              this.logMessageTmp = this.logMessageTmp
                  .replaceFirst(key.concat(" = "), key.concat(" = ").concat(value));
          }
        });
      } catch(Exception e) {
      }
      return this.logMessageTmp;
  }
}
