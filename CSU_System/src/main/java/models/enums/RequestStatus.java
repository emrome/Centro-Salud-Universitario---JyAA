package models.enums;

public enum RequestStatus implements LabelEnum {
	PENDING("Pendiente"),
	COMPLETED("Completada"),
	REJECTED("Rechazada");

	private final String label;

	RequestStatus(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
