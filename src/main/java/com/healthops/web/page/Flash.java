package com.healthops.web.page;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Toast messages carried across the post-redirect-get cycle. */
final class Flash {

    static final String OPEN_DIALOG = "openDialog";

    private Flash() {
    }

    static void success(RedirectAttributes ra, String message) {
        ra.addFlashAttribute("toast", message);
    }

    static void error(RedirectAttributes ra, String message) {
        ra.addFlashAttribute("toastError", message);
    }

    static boolean isDuplicate(DataIntegrityViolationException e) {
        return e instanceof DuplicateKeyException;
    }
}
