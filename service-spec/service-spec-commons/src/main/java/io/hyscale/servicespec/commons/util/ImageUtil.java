package io.hyscale.servicespec.commons.util;

import java.util.Objects;

import io.hyscale.servicespec.commons.fields.HyscaleSpecFields;
import io.hyscale.servicespec.commons.model.service.ServiceSpec;
import org.apache.commons.lang3.StringUtils;

import io.hyscale.commons.constants.ToolConstants;
import io.hyscale.commons.exception.HyscaleException;


public class ImageUtil {

    private static final String DELIMITER = "/";
    
    /**
     * get complete image name from service spec
     * @param serviceSpec
     * @return image complete name
     * registry url + "/" + imageName:tag
     * 1. registry url is null - imageName:tag
     * 2. registry url and tag not present - imageName
     * @throws HyscaleException
     */
    public static String getImage(ServiceSpec serviceSpec) throws HyscaleException {
        String tag = serviceSpec.get(HyscaleSpecFields.getPath(HyscaleSpecFields.image, HyscaleSpecFields.tag), String.class);
        String registryUrl = Objects.requireNonNullElse(serviceSpec.get(HyscaleSpecFields.getPath(HyscaleSpecFields.image, HyscaleSpecFields.registry), String.class), ToolConstants.EMPTY_STRING);
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(registryUrl)) {
            sb.append(registryUrl);
            sb.append(DELIMITER);
        }
        sb.append(serviceSpec.get(HyscaleSpecFields.image + "." + HyscaleSpecFields.name, String.class));
        if (StringUtils.isNotBlank(tag)) {
            sb.append(":");
            sb.append(tag);
        }
        return sb.toString();
    }
}