package famm.fammous.connection;

public class JResponserecordVideo {

		private String responseStatus;
		private String responseDevMessage;
		private String responseCode;
		private ResponseMetadata responseMetadata;
		
		public String getResponseStatus() {
			return responseStatus;
		}

		public String getResponseDevMessage() {
			return responseDevMessage;
		}

		public String getResponseCode() {
			return responseCode;
		}	
		

		@Override
		public String toString() {
			return "JResponseToken [responseStatus=" + responseStatus
					+ ", responseDevMessage=" + responseDevMessage
					+ ", responseCode=" + responseCode
					+ "]";
		}		
	
}
