package org.age.akka.core.helper;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.age.akka.core.exceptions.AkkaTemplateLoadingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.IOException;
import java.net.URL;

@Named
public class AkkaConfigurationLoader {

    private static final Logger log = LoggerFactory.getLogger(AkkaConfigurationLoader.class);

    private static final String templateLocation = "akka/config/template/configTemplate.txt";

    public String loadConfigurationTemplate() {
        try {
            URL url = Resources.getResource(templateLocation);
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            log.error("Could not load template");
            throw new AkkaTemplateLoadingException("Could not load template");
        }
    }

}
