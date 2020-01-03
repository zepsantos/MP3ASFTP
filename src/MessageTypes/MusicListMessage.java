package MessageTypes;

import Models.Music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicListMessage implements Message {
    private MessageTypes type = MessageTypes.MusicList;
    private int userID;
    private List<Music> musicList;
    private boolean validMessage = true;

    public MusicListMessage(List<Music> tmpList) {
        this.musicList = new ArrayList<>(tmpList);

    }

    public MusicListMessage(String response) {
        if(response != null) {
            Pattern pattern = Pattern.compile("\\[(.*);(.*)](.*)");
            Matcher match = pattern.matcher(response);
            if (match.matches()) {
                this.userID = Integer.parseInt(match.group(2));
                String musicListString = match.group(3);
                parseStringAndAddMusicInfo(musicListString);
            }
        } else {
            validMessage = false;
        }

    }

    private void parseStringAndAddMusicInfo(String musicListString) {
        this.musicList = new ArrayList<>();
        String[] musicInfo = musicListString.split("/");
        for(String s : musicInfo) {
            String[] musicMetaData = s.split(";");
            ArrayList<String> tags = new ArrayList<>( Arrays.asList(musicMetaData[4].split(",")));
            Music tmp = new Music(musicMetaData[0],musicMetaData[1],musicMetaData[2],Integer.parseInt(musicMetaData[5]),Integer.parseInt(musicMetaData[6]),Integer.parseInt(musicMetaData[7]),tags,musicMetaData[3]);
            this.musicList.add(tmp);
        }

    }

    @Override
    public MessageTypes getMessageType() {
        return type;
    }

    @Override
    public boolean isValidMessage() {
        return this.validMessage;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[').append(type.getType()).append(';').append(userID).append(']');
        for(Music m : musicList) {
            sb.append(m.toString());
            sb.append('/');
        }
        return sb.toString();
    }
}
