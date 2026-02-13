package models;

import jakarta.persistence.*;
import models.people.HealthStaff;
import models.people.SocialOrgRepresentative;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Report")
public class Report extends BaseEntity {

	@Column(nullable = false, length = 180)
	private String name;

	@Column(length = 4000)
	private String description;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	private HealthStaff author;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "file_content", columnDefinition = "LONGBLOB")
	private byte[] fileContent;

	@Column(name = "file_name", length = 255)
	private String fileName; // p. ej., "reporte_123.pdf"

	@Column(name = "file_mime", length = 100)
	private String fileMime; // p. ej., "application/pdf"

	@Column(name = "created_date", updatable = false)
	private LocalDate createdDate;

	@Column(name = "visible_to_all_healthstaff", nullable = false)
	private boolean visibleToAllHealthStaff = true;

	@Column(name = "public_visible", nullable = false)
	private boolean publicVisible = false;

	@PrePersist
	protected void onCreate() {
		this.createdDate = LocalDate.now();
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "report_shared_representatives",
			joinColumns = @JoinColumn(name = "report_id"),
			inverseJoinColumns = @JoinColumn(name = "representative_id")
	)
	private Set<SocialOrgRepresentative> sharedWith = new HashSet<>();

	public Report() {
		super();
	}

	public String getName() {
		return name;
	}

	public Report setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Report setDescription(String description) {
		this.description = description;
		return this;
	}

	public HealthStaff getAuthor() {
		return author;
	}

	public Report setAuthor(HealthStaff author) {
		this.author = author;
		return this;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public Report setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public Report setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public String getFileMime() {
		return fileMime;
	}

	public Report setFileMime(String fileMime) {
		this.fileMime = fileMime;
		return this;
	}

	public boolean isVisibleToAllHealthStaff() {
		return visibleToAllHealthStaff;
	}

	public Report setVisibleToAllHealthStaff(boolean visibleToAllHealthStaff) {
		this.visibleToAllHealthStaff = visibleToAllHealthStaff;
		return this;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public Report setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public boolean isPublicVisible() { return publicVisible; }
	public Report setPublicVisible(boolean publicVisible) {
		this.publicVisible = publicVisible;
		return this;
	}

	public Set<SocialOrgRepresentative> getSharedWith() {
		return sharedWith;
	}

	public Report setSharedWith(Set<SocialOrgRepresentative> sharedWith) {
		this.sharedWith = (sharedWith != null) ? sharedWith : new HashSet<>();
		return this;
	}

	public Report shareWith(SocialOrgRepresentative rep) {
		if (rep != null) {
			this.sharedWith.add(rep);
		}
		return this;
	}

	public Report revokeShareWith(SocialOrgRepresentative rep) {
		if (rep != null) {
			this.sharedWith.remove(rep);
		}
		return this;
	}

	public boolean canBeAccessedBy(Object user) {
		if (user == null) return false;

		if (user instanceof HealthStaff hs) {
			if (this.author != null && this.author.getId() != null && this.author.getId().equals(hs.getId())) {
				return true;
			}
			return this.visibleToAllHealthStaff;
		}

		if (user instanceof SocialOrgRepresentative rep) {
			return this.sharedWith.stream().anyMatch(r ->
					r != null && r.getId() != null && r.getId().equals(rep.getId()));
		}

		return false;
	}
}
