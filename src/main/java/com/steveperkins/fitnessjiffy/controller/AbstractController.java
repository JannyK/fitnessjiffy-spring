package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.config.SecurityConfig;
import com.steveperkins.fitnessjiffy.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public abstract class AbstractController {

    public static final String LOGIN_TEMPLATE = "login";
    static final String PROFILE_TEMPLATE = "profile";
    static final String FOOD_TEMPLATE = "food";
    static final String EXERCISE_TEMPLATE = "exercise";
    static final String REPORT_TEMPLATE = "report";

    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Used by child class controllers to obtain the currently authenticated user from Spring Security.
     */
    @Nullable
    final UserDTO currentAuthenticatedUser() {
        UserDTO userDTO = null;
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityConfig.SpringUserDetails) {
            final SecurityConfig.SpringUserDetails userDetails = (SecurityConfig.SpringUserDetails) authentication.getPrincipal();
            userDTO = userDetails.getUserDTO();
        }
        return userDTO;
    }

    @Nonnull
    final java.sql.Date stringToSqlDate(@Nonnull final String dateString) {
        java.sql.Date date;
        try {
            final Date utilDate = dateFormat.parse(dateString);
            date = new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            date = new java.sql.Date(new Date().getTime());
        }
        return date;
    }

    @Nonnull
    final java.sql.Date todaySqlDateForUser(@Nullable final UserDTO user) {
        if (user == null) {
            return new java.sql.Date(new Date().getTime());
        } else {
            final ZoneId timeZone = ZoneId.of(user.getTimeZone());
            final ZonedDateTime zonedDateTime = ZonedDateTime.now(timeZone);
            return new java.sql.Date(zonedDateTime.toLocalDate().atStartOfDay(timeZone).toInstant().toEpochMilli());
        }
    }

}
