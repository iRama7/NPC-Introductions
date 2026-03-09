package rama.npcintroductions;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Database {

    private Connection connection;

    private HashMap<Integer, List<String>> databaseMap;

    public Database(String path) throws SQLException {

        databaseMap = new HashMap<>();

        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS introductions
                    (id INT PRIMARY KEY,
                     players_clicked MEDIUMTEXT)
                    """);
        }

    }


    public boolean firstTime(int id, Player player) {
        boolean firstTime = true;

        if(databaseMap.containsKey(id)) {
            List<String> uuids = databaseMap.get(id);
            if(uuids.contains(player.getUniqueId().toString())) {
                firstTime = false;
            }
        }

        return firstTime;

    }

    public void addPlayer(int id, Player player) throws SQLException {

        if(!databaseMap.containsKey(id)){ //NEW NPC
            databaseMap.put(id, new ArrayList<>());
            addNPCToDB(id);
        }

        List<String> uuids = databaseMap.get(id);

        if(uuids == null){
            uuids = new ArrayList<>();
        }

        uuids.add(player.getUniqueId().toString());
        databaseMap.put(id, uuids);
        updateNPC(id);

    }

    public void resetPlayer(boolean all, String uuid) throws SQLException {

        if(all){
            for(int id : databaseMap.keySet()){
                databaseMap.put(id, new ArrayList<>());
                updateNPC(id);
            }
        }else {

            for (int id : databaseMap.keySet()) {

                if (databaseMap.get(id).contains(uuid)) {

                    databaseMap.get(id).remove(uuid);
                    updateNPC(id);

                }

            }
        }
    }

    private void updateNPC(int id) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE introductions SET id = ?, players_clicked = ? WHERE id = ?");
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, getDataAsString(id));
        preparedStatement.setInt(3, id);

        preparedStatement.executeUpdate();

    }

    private void addNPCToDB(int id){

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO introductions (id, players_clicked) VALUES (?, ?)")) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, null);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateFromFile() throws SQLException {

        HashMap<Integer, List<String>> map = new HashMap<>();

        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT * FROM introductions");
        ResultSet resultSet = statement.executeQuery();

        while(resultSet.next()) {
            int id = resultSet.getInt("id");
            String data =  resultSet.getString("players_clicked");
            map.put(id, getDataAsList(data));
        }

        databaseMap = map;
    }

    private List<String> getDataAsList(String data){ //[0ca58c02-1fd0-48ac-aca8-f97d1749bd4c, 0ca58c02-1fd0-48ac-aca8-f97d1749bd4c, ]

        data = data.replace("[", "").replace("]", "");
        String[] split = data.split(", ");
        List<String> list = new ArrayList<>(Arrays.asList(split));

        return list;
    }

    @Nullable
    private String getDataAsString(int id){ //[0ca58c02-1fd0-48ac-aca8-f97d1749bd4c, 0ca58c02-1fd0-48ac-aca8-f97d1749bd4c, ]

        List<String> uuids = databaseMap.get(id);
        if(uuids == null){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(String uuid : uuids){
            sb.append(uuid).append(", ");
        }
        sb.append("]");

        return sb.toString();

    }




}
