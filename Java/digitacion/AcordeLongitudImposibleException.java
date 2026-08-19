package digitacion;

public class AcordeLongitudImposibleException extends IllegalArgumentException {
	private String acorde;
	public AcordeLongitudImposibleException(String ac) {
		acorde = ac;
	}
	
	public String getAcordeRaro() {
		return acorde;
	}

}
