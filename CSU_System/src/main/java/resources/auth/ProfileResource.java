package resources.auth;

import daos.AuthUserDAO;
import dtos.people.*;
import exceptions.InvalidDataException;
import exceptions.ResourceNotFoundException;
import exceptions.UnauthorizedException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import models.people.Admin;
import models.people.HealthStaff;
import models.people.SocialOrgRepresentative;
import models.people.User;
import services.people.AdminService;
import services.people.HealthStaffService;
import services.people.SocialOrgRepresentativeService;
import utils.PasswordHasher;
import utils.JwtUtil;
import io.jsonwebtoken.Claims;

import jakarta.servlet.http.HttpServletRequest;

@Path("/me")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProfileResource {

    @Context
    SecurityContext securityContext;

    @Context
    HttpServletRequest httpRequest;

    @Context
    HttpHeaders httpHeaders;

    @Inject AuthUserDAO authUserDAO;
    @Inject AdminService adminService;
    @Inject HealthStaffService healthStaffService;
    @Inject SocialOrgRepresentativeService repService;
    @Inject PasswordHasher passwordHasher;

    private User requireCurrentUser() {
        String sub = (securityContext != null && securityContext.getUserPrincipal() != null)
                ? securityContext.getUserPrincipal().getName()
                : null;

        if (sub == null && httpRequest != null) {
            Object uid = httpRequest.getAttribute("userId");
            if (uid != null) sub = String.valueOf(uid);
        }

        if (sub == null && httpHeaders != null) {
            String auth = httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring("Bearer".length()).trim();
                Claims claims = JwtUtil.getClaims(token);
                sub = claims.getSubject();
            }
        }

        if (sub == null) throw new UnauthorizedException("No autenticado");

        User user;
        try {
            Long id = Long.valueOf(sub);
            user = authUserDAO.findById(id);
        } catch (NumberFormatException ignore) {
            user = authUserDAO.findByEmail(sub).orElse(null);
        }
        if (user == null) throw new ResourceNotFoundException("Usuario actual no encontrado");
        return user;
    }

    @GET
    public Response getMe() {
        User u = requireCurrentUser();
        Object dto;
        String type;
        if (u instanceof Admin a) {
            dto = adminService.getById(a.getId());
            type = "ADMIN";
        } else if (u instanceof HealthStaff h) {
            dto = healthStaffService.getById(h.getId());
            type = "HEALTHSTAFF";
        } else if (u instanceof SocialOrgRepresentative r) {
            dto = repService.getById(r.getId());
            type = "REPRESENTATIVE";
        } else {
            throw new InvalidDataException("Tipo de usuario no soportado");
        }
        return Response.ok(new MeResponse(type, dto)).build();
    }

    @PUT
    public Response updateMe(Object body) {
        User u = requireCurrentUser();
        if (u instanceof Admin a) {
            AdminDTO dto = (AdminDTO) JsonHelper.convert(body, AdminDTO.class);
            dto.setId(a.getId());
            dto.setEmail(a.getEmail());
            dto.setRegistrationDate(a.getRegistrationDate());
            dto.setEnabled(a.isEnabled());
            return Response.ok(adminService.update(a.getId(), dto)).build();
        } else if (u instanceof HealthStaff h) {
            HealthStaffDTO dto = (HealthStaffDTO) JsonHelper.convert(body, HealthStaffDTO.class);
            dto.setId(h.getId());
            dto.setEmail(h.getEmail());
            dto.setRegistrationDate(h.getRegistrationDate());
            dto.setEnabled(h.isEnabled());
            dto.setSpecialty(h.getSpecialty());
            dto.setLicense(h.getLicense());
            return Response.ok(healthStaffService.update(h.getId(), dto)).build();
        } else if (u instanceof SocialOrgRepresentative r) {
            SocialOrgRepresentativeDTO dto = (SocialOrgRepresentativeDTO) JsonHelper.convert(body, SocialOrgRepresentativeDTO.class);
            dto.setId(r.getId());
            dto.setEmail(r.getEmail());
            dto.setRegistrationDate(r.getRegistrationDate());
            dto.setEnabled(r.isEnabled());
            dto.setOrganizationId(r.getOrganization() != null ? r.getOrganization().getId() : null);
            return Response.ok(repService.update(r.getId(), dto)).build();
        }
        throw new InvalidDataException("Tipo de usuario no soportado");
    }

    @PUT
    @Path("/password")
    public Response changePassword(ChangePasswordDTO dto) {
        if (dto == null || dto.getOldPassword() == null || dto.getNewPassword() == null || dto.getConfirmNewPassword() == null) {
            throw new InvalidDataException("Datos de contraseña incompletos");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new InvalidDataException("Las nuevas contraseñas no coinciden");
        }
        User u = requireCurrentUser();
        if (!passwordHasher.check(dto.getOldPassword(), u.getPassword())) {
            throw new UnauthorizedException("La contraseña actual no es correcta");
        }
        u.setPassword(passwordHasher.hash(dto.getNewPassword()));
        authUserDAO.update(u);
        return Response.noContent().build();
    }

    public static class MeResponse {
        private String type;
        private Object data;
        public MeResponse() {}
        public MeResponse(String type, Object data) { this.type = type; this.data = data; }
        public String getType() { return type; }
        public Object getData() { return data; }
        public void setType(String type) { this.type = type; }
        public void setData(Object data) { this.data = data; }
    }

    static class JsonHelper {
        private static final jakarta.json.bind.Jsonb jsonb = jakarta.json.bind.JsonbBuilder.create();
        static <T> T convert(Object src, Class<T> target) {
            String json = jsonb.toJson(src);
            return jsonb.fromJson(json, target);
        }
    }
}
