package com.project.fitness.security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

// Custom request wrapper that intercepts `getParameter`, `getHeader`, and the input stream to escape HTML tags preventing XSS (Cross Site Scripting) payloads.
public class XssRequestWrapper extends HttpServletRequestWrapper {

  private byte[] rawData;

  public XssRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    // Cache request body so downstream can read it multiple times.
    // IMPORTANT: Do not HTML-escape JSON bodies here. Escaping breaks valid JSON syntax.
    try {
      this.rawData = StreamUtils.copyToByteArray(request.getInputStream());
    } catch (IOException e) {
       this.rawData = new byte[0];
    }
  }

  @Override
  public String[] getParameterValues(String parameter) {
    String[] values = super.getParameterValues(parameter);
    if (values == null) {
      return null;
    }
    int count = values.length;
    String[] encodedValues = new String[count];
    for (int i = 0; i < count; i++) {
        encodedValues[i] = cleanXss(values[i]);
    }
    return encodedValues;
  }

  @Override
  public String getParameter(String parameter) {
    String value = super.getParameter(parameter);
    return cleanXss(value);
  }

  @Override
  public String getHeader(String name) {
    String value = super.getHeader(name);
    // Preserve security and protocol headers exactly as sent.
    if ("authorization".equalsIgnoreCase(name)
        || "sec-websocket-protocol".equalsIgnoreCase(name)
        || "sec-websocket-key".equalsIgnoreCase(name)) {
      return value;
    }
    return cleanXss(value);
  }

  @Override
  public ServletInputStream getInputStream() {
    return new ServletInputStream() {
        private final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawData);

        @Override
        public int read() {
            return byteArrayInputStream.read();
        }

        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("Not implemented");
        }
    };
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
  }

  // Escape HTML entities. e.g. <script> becomes &lt;script&gt;
  private String cleanXss(String value) {
    if (value == null) {
      return null;
    }
    return HtmlUtils.htmlEscape(value);
  }
}
