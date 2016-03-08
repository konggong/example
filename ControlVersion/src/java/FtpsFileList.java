
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class FtpsFileList {

    private static final Logger LOG = LoggerFactory.getLogger(FtpsFileList.class);

    public static void main(String[] args) {
        //listFileNames("10.22.13.63", 22, "pww", "pww", "/home/pww");
        String str = readFile("10.22.13.63", 22, "pww", "pww", "/home/4gl/client/src/jpfr001.4gl");
        System.out.println(str);
    }

    private static String readFile(String host, int port, String username, final String password, String filename) {
        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;

        StringBuilder result = new StringBuilder();

        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            LOG.debug("Session connected!");
            channel = sshSession.openChannel("sftp");
            channel.connect();
            LOG.debug("Channel connected!");
            sftp = (ChannelSftp) channel;

            InputStream obj_InputStream = sftp.get(filename);
            char[] ch_Buffer = new char[0x10000];
            Reader reader = new InputStreamReader(obj_InputStream, "UTF-8");
            int int_Line = 0;
            do {
                int_Line = reader.read(ch_Buffer, 0, ch_Buffer.length);
                if (int_Line > 0) {
                    result.append(ch_Buffer, 0, int_Line);
                }
            } while (int_Line >= 0);
            reader.close();

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        } finally {
            closeChannel(sftp);
            closeChannel(channel);
            closeSession(sshSession);
        }

        return result.toString();

    }

    private static List<String> listFileNames(String host, int port, String username, final String password, String dir) {
        List<String> list = new ArrayList<String>();
        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            LOG.debug("Session connected!");
            channel = sshSession.openChannel("sftp");
            channel.connect();
            LOG.debug("Channel connected!");
            sftp = (ChannelSftp) channel;
            Vector<?> vector = sftp.ls(dir);
            for (Object item : vector) {
                LsEntry entry = (LsEntry) item;
                System.out.println(entry.getFilename());
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        } finally {
            closeChannel(sftp);
            closeChannel(channel);
            closeSession(sshSession);
        }
        return list;
    }

    private static void closeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    private static void closeSession(Session sshSession) {
        if (sshSession != null) {
            if (sshSession.isConnected()) {
                sshSession.disconnect();
            }
        }
    }
}
