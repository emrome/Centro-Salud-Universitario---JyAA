package daos;

import models.Report;

import java.util.List;

public interface ReportDAO extends GenericDAO<Report> {
    List<Report> findAllPublic();
}