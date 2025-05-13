package com.example.demo.repository;

import com.example.demo.model.Stamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class StampRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Stamp> findAll() {
        return jdbcTemplate.query("SELECT * FROM Stamps", new StampRowMapper());
    }

    public List<Stamp> findAllByUserId(String userId) {
        String sql = "SELECT * FROM Stamps WHERE userId = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new StampRowMapper());
    }

    public Stamp findById(String stampId) {
        String sql = "SELECT * FROM Stamps WHERE stampId = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{stampId}, new StampRowMapper());
    }

    public Stamp save(Stamp stamp) {
        // 检查用户是否存在
        String checkUserSql = "SELECT COUNT(*) FROM Users WHERE userId = ?";
        int userCount = jdbcTemplate.queryForObject(checkUserSql, new Object[]{stamp.getUserId()}, Integer.class);

        if (userCount == 0) {
            // 用户不存在，新建用户
            String createUserSql = "INSERT INTO Users (userId, userName) VALUES (?, ?)";
            jdbcTemplate.update(createUserSql, stamp.getUserId(), "DefaultUserName"); // 使用默认用户名或从其他地方获取
            // TBD，如果用户不存在那用户名需不需要获取
        }

        if(stamp.getHandwritten()!=null) {
            String sql = "INSERT INTO Stamps (stampId, userId, style, color, wrapText, horizonText, handwritten, stampImage, isDefault) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, stamp.getStampId(), stamp.getUserId(), stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText(), stamp.getHandwritten(), stamp.getStampImage(), stamp.isDefault());
        }
        else{
            String sql = "INSERT INTO Stamps (stampId, userId, style, color, wrapText, horizonText, handwritten, stampImage, isDefault) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, stamp.getStampId(), stamp.getUserId(), stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText(), stamp.getHandwritten(), stamp.getStampImage(), stamp.isDefault());
        }
        // 新建印章

        return stamp;
    }

    public void update(Stamp stamp) {
        String sql = "UPDATE Stamps SET userId = ?, style = ?, color = ?, wrapText = ?, horizonText = ?, handwritten = ?, stampImage = ?, isDefault = ? WHERE stampId = ?";
        jdbcTemplate.update(sql, stamp.getUserId(), stamp.getStyle(), stamp.getColor(), stamp.getWrapText(), stamp.getHorizonText(), stamp.getHandwritten(), stamp.getStampImage(), stamp.isDefault(), stamp.getStampId());
    }

    public void delete(String stampId) {
        String sql = "DELETE FROM Stamps WHERE stampId = ?";
        jdbcTemplate.update(sql, stampId);
    }

    public void setDefaultStamp(String userId, String stampId) {
        String resetSql = "UPDATE Stamps SET isDefault = FALSE WHERE userId = ? AND stampId != ?";
        jdbcTemplate.update(resetSql, userId, stampId);

        String setDefaultSql = "UPDATE Stamps SET isDefault = TRUE WHERE userId = ? AND stampId = ?";
        jdbcTemplate.update(setDefaultSql, userId, stampId);
    }

    // 内部类，用于映射ResultSet到Stamp对象
    private static class StampRowMapper implements RowMapper<Stamp> {
        @Override
        public Stamp mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stamp stamp = new Stamp();
            stamp.setStampId(rs.getString("stampId"));
            stamp.setUserId(rs.getString("userId"));
            stamp.setStyle(rs.getString("style"));
            stamp.setColor(rs.getString("color"));
            stamp.setWrapText(rs.getString("wrapText"));
            stamp.setHorizonText(rs.getString("horizonText"));
            stamp.setHandwritten(rs.getString("handwritten"));
            stamp.setStampImage(rs.getString("stampImage"));
            stamp.setDefault(rs.getBoolean("isDefault"));
            return stamp;
        }
    }
}