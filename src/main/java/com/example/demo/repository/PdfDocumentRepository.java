package com.example.demo.repository;

import com.example.demo.model.Document;
import com.example.demo.model.SeamConfiguration;
import com.example.demo.model.StampPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class PdfDocumentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 保存文档信息
    public void saveDocument(Document document) {
        String sql = "INSERT INTO Documents (documentId, userId, documentName, documentFile) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, document.getDocumentId(), document.getUserId(), document.getDocumentName(), document.getDocumentFile());
    }

    // 保存签章位置信息
    public void saveStampPosition(StampPosition position) {
        String sql = "INSERT INTO StampPositions (documentId, stampId, page, x, y) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, position.getDocumentId(), position.getStampId(), position.getPage(), position.getX(), position.getY());
    }

    // 保存骑缝配置信息
    public void saveSeamConfiguration(SeamConfiguration config) {
        String sql = "INSERT INTO SeamConfigurations (documentId, seamType, crossPages) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, config.getDocumentId(), config.getSeamType(), config.getCrossPages());
    }

    // 查询所有文档
    public List<Document> findAllDocuments() {
        String sql = "SELECT * FROM Documents";
        return jdbcTemplate.query(sql, new DocumentRowMapper());
    }

    // 查询特定文档
    public Document findDocumentById(String documentId) {
        String sql = "SELECT * FROM Documents WHERE documentId = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{documentId}, new DocumentRowMapper());
    }

    // 查询签章位置
    public List<StampPosition> findAllStampPositions() {
        String sql = "SELECT * FROM StampPositions";
        return jdbcTemplate.query(sql, new StampPositionRowMapper());
    }

    // 查询特定文档的签章位置
    public List<StampPosition> findStampPositionsByDocumentId(String documentId) {
        String sql = "SELECT * FROM StampPositions WHERE documentId = ?";
        return jdbcTemplate.query(sql, new Object[]{documentId}, new StampPositionRowMapper());
    }

    // 查询骑缝配置
    public List<SeamConfiguration> findAllSeamConfigurations() {
        String sql = "SELECT * FROM SeamConfigurations";
        return jdbcTemplate.query(sql, new SeamConfigurationRowMapper());
    }

    // 查询特定文档的骑缝配置
    public SeamConfiguration findSeamConfigurationByDocumentId(String documentId) {
        String sql = "SELECT * FROM SeamConfigurations WHERE documentId = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{documentId}, new SeamConfigurationRowMapper());
    }

    // 自定义RowMapper
    private static class DocumentRowMapper implements RowMapper<Document> {
        @Override
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document document = new Document();
            document.setDocumentId(rs.getString("documentId"));
            document.setUserId(rs.getString("userId"));
            document.setDocumentName(rs.getString("documentName"));
            document.setDocumentFile(rs.getBytes("documentFile"));
            return document;
        }
    }

    private static class StampPositionRowMapper implements RowMapper<StampPosition> {
        @Override
        public StampPosition mapRow(ResultSet rs, int rowNum) throws SQLException {
            StampPosition position = new StampPosition();
            position.setPositionId(rs.getInt("positionId"));
            position.setDocumentId(rs.getString("documentId"));
            position.setStampId(rs.getString("stampId"));
            position.setPage(rs.getInt("page"));
            position.setX(rs.getFloat("x"));
            position.setY(rs.getFloat("y"));
            return position;
        }
    }

    private static class SeamConfigurationRowMapper implements RowMapper<SeamConfiguration> {
        @Override
        public SeamConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
            SeamConfiguration config = new SeamConfiguration();
            config.setSeamConfigId(rs.getInt("seamConfigId"));
            config.setDocumentId(rs.getString("documentId"));
            config.setSeamType(rs.getString("seamType"));
            config.setCrossPages(rs.getString("crossPages"));
            return config;
        }
    }
}