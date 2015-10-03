import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *@scr
 *@dbo.use GameScore
 */
public class MobApp{
   /**
     * @scr.label "Test"
     * @scr.firstDisplay
     * @scr.exitButton
     * @scr.textField txtScreen
     */
    private Form form;

    /**
     * @scr.label "GameScores"
     * @scr.string createHighScore()
     * @scr.maxSize 1024
     */
    public TextField txtScreen;

    /************* Application-logic ********************/

    public String createHighScore(){
        String sb = "";

        try{
            GameScores.store(new GameScore("Alice", 100, "wow!"));
            GameScores.store(new GameScore("Bill", 120, "wow! wow!"));
            GameScores.store(new GameScore("Candice", 80, "wow!"));
            GameScores.store(new GameScore("Dean", 40, "wow!"));
            GameScores.store(new GameScore("Ethel", 200, "wow!"));
            GameScores.store(new GameScore("Mike", 200, "wow!"));
            GameScores.store(new GameScore("Farnsworth", 110, "wow!"));
            GameScores.store(new GameScore("Farnsworth", 220, "wow!"));

            System.out.println(GameScores.exists(new GameScore("Farnsworth", 110, "wow!")));
            System.out.println(GameScores.exists(new GameScore("Farnsworth", 110, "wow2!")));
            System.out.println(GameScores.exists(new GameScore("Farnsworth", 220, "wow!")));

            java.util.Vector scores = GameScores.retrieve(new GameScore(), true); //all
            for(int i = 0; i < scores.size(); i++) {
                GameScore o = (GameScore)scores.elementAt(i);
                sb = sb+o.toString()+"\n";
                sb = sb;
            }
            sb = sb+"\n-----------\n";

            scores = GameScores.retrieve(new GameScore("Farnsworth", GameScore.AllScore, GameScore.AllComment), false); // one
            for(int i = 0; i < scores.size(); i++) {
                GameScore o = (GameScore)scores.elementAt(i);
                sb = sb+o.toString()+"\n";
            }

        } catch(Exception ex) {
            sb = sb+ex.toString();
        }


        if(sb.length() > 1024) sb = sb.substring(0, 1024);
        return sb;
    }

}
