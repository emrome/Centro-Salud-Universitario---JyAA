package dtos;

public abstract class GenericDTO {
    private Long id;
    private boolean isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean isDeleted) { this.isDeleted = isDeleted;}
}
