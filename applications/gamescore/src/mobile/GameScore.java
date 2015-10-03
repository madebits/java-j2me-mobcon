/**
 * @dbo
 */
public class GameScore
{
    /**
     * @dbo.pk
     * @dbo.min 1
     * @dbo.max 256
     * @dbo.sort asc
     */
    private String playerName;

    /**
     * @dbo.pk
     * @dbo.min 0
     * @dbo.max 10000
     * @dbo.sort des
     */
    private int score;

    /**
     * @dbo.min 0
     * @dbo.max 512
     */
    private String comment;
}