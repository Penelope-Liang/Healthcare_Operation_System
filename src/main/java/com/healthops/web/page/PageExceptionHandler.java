package com.healthops.web.page;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/** Renders a friendly page instead of a stack trace when MySQL is not reachable. */
@ControllerAdvice(basePackageClasses = PageExceptionHandler.class)
public class PageExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(PageExceptionHandler.class);

    @ExceptionHandler(DataAccessResourceFailureException.class)
    ModelAndView databaseUnavailable(DataAccessResourceFailureException e, HttpServletRequest request) {
        log.warn("Database unavailable while rendering {}: {}", request.getRequestURI(), e.getMostSpecificCause().getMessage());
        ModelAndView view = new ModelAndView("database-unavailable");
        view.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        view.addObject("detail", e.getMostSpecificCause().getMessage());
        return view;
    }
}
