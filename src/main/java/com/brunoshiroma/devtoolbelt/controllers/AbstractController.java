package com.brunoshiroma.devtoolbelt.controllers;

import com.brunoshiroma.devtoolbelt.config.DevtoolbeltConfigBean;
import org.springframework.ui.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public abstract class AbstractController {

    private final DevtoolbeltConfigBean configBean;

    protected final SimpleDateFormat releaseDateParser = new SimpleDateFormat("yyyyMMdd-HHmmss");

    public static final String HTML_TITLE = "htmlTitle";

    public static final String HTML_DESCRIPTION = "htmlDescription";

    protected AbstractController(DevtoolbeltConfigBean configBean) {
        this.configBean = configBean;
    }

    protected void setUpModel(Model model){
        try {
            Date lastChangeDate = releaseDateParser.parse(configBean.getRelease());
            Calendar lastChangeCal = Calendar.getInstance();
            lastChangeCal.setTime(lastChangeDate);

            model.addAttribute("changeYear", String.valueOf(lastChangeCal.get(Calendar.YEAR)));
            model.addAttribute("CACHE_VERSION", configBean.getRelease());
        } catch (ParseException e) {
            Logger.getGlobal().throwing("StaticController", "setUpModel", e);
        }
    }

}
