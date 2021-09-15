package bot.commands;

import bot.api.ApiConstants;
import bot.dto.MessageEventDTO;
import bot.dto.player.Player;
import bot.graphics.GraphicsConstants;
import bot.graphics.ProfileImage;
import bot.main.BotConstants;
import bot.utils.GraphicsUtils;
import bot.utils.JavaFXUtils;
import bot.utils.Messages;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class Profile {

    public void sendProfileImage(Player player, MessageEventDTO event) {
        if (player == null) {
            System.out.println("no player");
            return;
        }
        BufferedImage qrCodeImage = GraphicsUtils.generateQRCode(ApiConstants.USER_PRE_URL+player.getPlayerId(), GraphicsConstants.profileQrCodeWidth, GraphicsConstants.profileQrCodeHeight);
        if (qrCodeImage == null) {
            //error
            return;
        }
        String playerId = player.getPlayerId();
        String messageId = event.getMessage().getId();
        String fileName = "profile_" + playerId + "_" + messageId + ".png";
        String filePath = BotConstants.RESOURCES_PATH + fileName;
        ProfileImage.setQrCode(qrCodeImage);
        ProfileImage.setPlayer(player);
        ProfileImage.setFilePath(filePath);
        JavaFXUtils.launch(ProfileImage.class);

        File profileFile = new File(filePath);
        int profileWaitingCounter = 0;
        ProfileImage.setFinished(false); //Timing Problem Fix
        while (!ProfileImage.isFinished()) {
            if (profileWaitingCounter > 30) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            profileWaitingCounter++;
        }
        if (profileFile.exists()) {
            Messages.sendImage(profileFile, fileName, event.getChannel());
            profileFile.deleteOnExit();
        }
    }
}
