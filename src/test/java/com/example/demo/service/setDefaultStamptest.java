package com.example.demo.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class setDefaultStamptest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private StampService stampService;

    @Test
    public void testSetDefaultStampWithEmptyUserId() {
        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("", "validStampId");
        });
    }

    @Test
    public void testSetDefaultStampWithEmptyStampId() {
        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("validUserId", "");
        });
    }

    @Test
    public void testSetDefaultStampWithEmptyUserIdAndStampId() {
        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("", "");
        });
    }

    @Test
    public void testSetDefaultStampWithNonExistentStampId() {
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);

        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("user123", "nonExistentStampId");
        });
    }

    @Test
    public void testSetDefaultStampWithStampIdNotBelongingToUser() {
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);

        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("user123", "stampIdBelongingToAnotherUser");
        });
    }

    @Test
    public void testSetDefaultStampWithSpecialCharactersInUserId() {
        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("user!@#", "acfdcfdc-e826-4db9-98c6-1c4104cdc98d");
        });
    }

    @Test
    public void testSetDefaultStampWithSpecialCharactersInStampId() {
        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("user123", "stamp!@#");
        });
    }

    @Test
    public void testSetDefaultStampWithWhitespaceUserId() {
        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("   ", "acfdcfdc-e826-4db9-98c6-1c4104cdc98d");
        });
    }

    @Test
    public void testSetDefaultStampWithWhitespaceStampId() {
        assertThrows(IllegalArgumentException.class, () -> {
            stampService.setDefaultStamp("user123", "   ");
        });
    }
}
