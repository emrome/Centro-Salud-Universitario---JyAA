package dtos.people;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO to change current user's password")
public class ChangePasswordDTO {
    @Schema(required = true) private String oldPassword;
    @Schema(required = true) private String newPassword;
    @Schema(required = true) private String confirmNewPassword;

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmNewPassword() { return confirmNewPassword; }
    public void setConfirmNewPassword(String confirmNewPassword) { this.confirmNewPassword = confirmNewPassword; }
}
