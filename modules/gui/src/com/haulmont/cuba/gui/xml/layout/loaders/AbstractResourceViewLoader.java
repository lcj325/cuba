/*
 * Copyright (c) 2008-2017 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.gui.xml.layout.loaders;

import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.*;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractResourceViewLoader<T extends ResourceView> extends AbstractComponentLoader<T> {

    @Override
    public void loadComponent() {
        assignXmlDescriptor(resultComponent, element);

        loadVisible(resultComponent, element);
        loadEnable(resultComponent, element);

        loadStyleName(resultComponent, element);

        loadCaption(resultComponent, element);
        loadDescription(resultComponent, element);

        loadWidth(resultComponent, element);
        loadHeight(resultComponent, element);
        loadAlign(resultComponent, element);

        loadResource(resultComponent, element);

        loadAlternateText(resultComponent, element);
    }

    protected void loadResource(ResourceView image, Element element) {
        if (loadFileImageResource(image, element)) return;

        if (loadThemeImageResource(image, element)) return;

        if (loadClasspathImageResource(image, element)) return;

        if (loadRelativePathImageResource(image, element)) return;

        loadUrlImageResource(image, element);
    }

    protected boolean loadRelativePathImageResource(ResourceView resultComponent, Element element) {
        Element relativePath = element.element("relativePath");
        if (relativePath == null)
            return false;

        String path = relativePath.attributeValue("path");
        if (StringUtils.isEmpty(path)) {
            throw new GuiDevelopmentException("No path provided for the RelativePathImageResource", context.getFullFrameId());
        }

        RelativePathResource resource = resultComponent.createResource(RelativePathResource.class);

        resource.setPath(path);

        loadMimeType(resource, relativePath);

        resultComponent.setSource(resource);

        return true;
    }

    protected void loadUrlImageResource(ResourceView resultComponent, Element element) {
        Element urlResource = element.element("url");
        if (urlResource == null)
            return;

        String url = urlResource.attributeValue("url");
        if (StringUtils.isEmpty(url)) {
            throw new GuiDevelopmentException("No url provided for the UrlImageResource", context.getFullFrameId());
        }

        UrlResource resource = resultComponent.createResource(UrlResource.class);
        try {
            resource.setUrl(new URL(url));

            loadMimeType(resource, urlResource);

            resultComponent.setSource(resource);
        } catch (MalformedURLException e) {
            String msg = String.format("An error occurred while creating UrlImageResource with the given url: %s", url);
            throw new GuiDevelopmentException(msg, context.getFullFrameId());
        }
    }

    protected boolean loadClasspathImageResource(ResourceView resultComponent, Element element) {
        Element classpathResource = element.element("classpath");
        if (classpathResource == null)
            return false;

        String classpathPath = classpathResource.attributeValue("path");
        if (StringUtils.isEmpty(classpathPath)) {
            throw new GuiDevelopmentException("No path provided for the ClasspathImageResource", context.getFullFrameId());
        }

        ClasspathResource resource = resultComponent.createResource(ClasspathResource.class);

        resource.setPath(classpathPath);

        loadMimeType(resource, classpathResource);
        loadStreamSettings(resource, classpathResource);

        resultComponent.setSource(resource);

        return true;
    }

    protected boolean loadThemeImageResource(ResourceView resultComponent, Element element) {
        Element themeResource = element.element("theme");
        if (themeResource == null)
            return false;

        String themePath = themeResource.attributeValue("path");
        if (StringUtils.isEmpty(themePath)) {
            throw new GuiDevelopmentException("No path provided for the ThemeImageResource", context.getFullFrameId());
        }

        resultComponent.setSource(ThemeResource.class).setPath(themePath);

        return true;
    }

    protected boolean loadFileImageResource(ResourceView resultComponent, Element element) {
        Element fileResource = element.element("file");
        if (fileResource == null)
            return false;

        String filePath = fileResource.attributeValue("path");
        if (StringUtils.isEmpty(filePath)) {
            throw new GuiDevelopmentException("No path provided for the FileImageResource", context.getFullFrameId());
        }

        File file = new File(filePath);
        if (!file.exists()) {
            String msg = String.format("Can't load FileImageResource. File with given path does not exists: %s", filePath);
            throw new GuiDevelopmentException(msg, context.getFullFrameId());
        }

        FileResource resource = resultComponent.createResource(FileResource.class);

        resource.setFile(file);

        loadStreamSettings(resource, fileResource);

        resultComponent.setSource(resource);

        return true;
    }

    protected void loadMimeType(ResourceView.HasMimeType resource, Element resourceElement) {
        String mimeType = resourceElement.attributeValue("mimeType");
        if (StringUtils.isNotEmpty(mimeType)) {
            resource.setMimeType(mimeType);
        }
    }

    protected void loadStreamSettings(ResourceView.HasStreamSettings resource, Element resourceElement) {
        String cacheTime = resourceElement.attributeValue("cacheTime");
        if (StringUtils.isNotEmpty(cacheTime)) {
            resource.setCacheTime(Long.parseLong(cacheTime));
        }

        String bufferSize = resourceElement.attributeValue("bufferSize");
        if (StringUtils.isNotEmpty(bufferSize)) {
            resource.setBufferSize(Integer.parseInt(bufferSize));
        }
    }

    protected void loadAlternateText(ResourceView resultComponent, Element element) {
        String alternateText = element.attributeValue("alternateText");
        if (StringUtils.isNotEmpty(alternateText)) {
            resultComponent.setAlternateText(alternateText);
        }
    }
}