/*
 * MIT License
 *
 * Copyright (c) 2016 Will Warren
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.willwarren;

import com.willwarren.exceptions.NetworkException;
import com.willwarren.exceptions.ResponseException;
import com.willwarren.exceptions.ResponseMalformedException;
import com.willwarren.exceptions.WorkqException;
import com.willwarren.model.ForegroundJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.Socket;

public class WorkqClient {

    private final Log LOG = LogFactory.getLog(WorkqClient.class);

    private final static String CRLF = "\r\n";

    private String encoding = "UTF-8";
    private String host;
    private int port;
    private Socket socket;

    public WorkqClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

        try {
            socket = new Socket(this.host, this.port);
        } catch (IOException ioe) {
            LOG.error("Error connecting to workq host", ioe);
            throw ioe;
        }
    }

    /**
     * Submit a @link{@link ForegroundJob} and wait for the result.
     * Wraps the "run" command: https://github.com/iamduo/workq/blob/master/doc/protocol.md#run
     *
     * @param job {@link ForegroundJob} The Job to run
     * @return The number of replies
     * @throws WorkqException, NetworkException, ResponseMalformedException
     */
    public Integer run(ForegroundJob job) throws WorkqException {

        // If the priority was passed into the job, we have to append it to the command
        String extraFlags = "";
        if (job.getPriority() != null) {
            extraFlags = String.format(" -priority=%d", job.getPriority());
        }

        try {

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            String convertedCommand = String.format(
                    "run %s %s %d %d %d%s%s%s%s",
                    job.getId().toString(),
                    job.getName(),
                    job.getTtr(),
                    job.getTimeout(),
                    job.getPayload().length,
                    extraFlags,
                    CRLF,
                    new String(job.getPayload()),
                    CRLF
            );

            LOG.info(convertedCommand);
            outputStreamWriter.write(convertedCommand);
            outputStreamWriter.flush();

            BufferedReader responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // TODO: Get more than just the first line. Also get the response payload
            String statusLine = responseReader.readLine();

            if (statusLine.length() < 5) {
                throw new ResponseMalformedException();
            }

            String sign = statusLine.substring(0, 1);

            if ("+".equals(sign) && "OK".equals(statusLine.substring(1, 3))) {
                return Integer.valueOf(statusLine.substring(4));
            }

            if (!"-".equals(sign)) {
                throw new ResponseMalformedException();
            }

            errorFromLine(statusLine);
        } catch (UnsupportedEncodingException uee) {
            LOG.error("Invalid encoding used to convert payload", uee);
            throw new WorkqException("Invalid encoding used to convert payload", uee);
        } catch (IOException ioe) {
            LOG.error("Error writing command to server", ioe);
            throw new NetworkException("Error writing command to server", ioe);
        } catch (NumberFormatException nfe) {
            LOG.error("Response was malformed", nfe);
            throw new ResponseMalformedException();
        }

        return 0;
    }

    private void errorFromLine(String line) throws ResponseMalformedException, ResponseException {
        // The format is -CODE TEXT
        String[] parts = line.split(" ", 2);

        String code = parts[0];
        String text = "";

        if (code.length() <= 1) {
            throw new ResponseMalformedException();
        }

        // Strip the "-" off the beginning
        code = code.substring(1);

        if (parts.length == 2) {
            text = parts[1];

            if (text.length() == 0) {
                throw new ResponseMalformedException();
            }
        }

        throw new ResponseException(code, text);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
