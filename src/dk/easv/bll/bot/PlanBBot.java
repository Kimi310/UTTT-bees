package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dk.easv.bll.field.IField.AVAILABLE_FIELD;

public class PlanBBot implements IBot{
    private int depth = 5;
    private int botId;
    @Override
    public IMove doMove(IGameState state) {

        System.out.println(Arrays.deepToString(findMicroBoard(state.getField().getMacroboard(),state)));
        if (state.getMoveNumber()==0){
            botId =0;
            return new Move(4,4);
        } else if (state.getMoveNumber()==1) {
            botId=1;
        }

        return new Move(0,0);
    }

    @Override
    public String getBotName() {
        return "PlanBBot";
    }

    private int evaluatePosition(String[][] currentMicroboard, int currentDepth, int turn){
        if (currentDepth==depth){
            return 0;
        }else {
            for (int i=0;i<3;i++){
                for (int j=0;j<3;j++){
                    if (Objects.equals(currentMicroboard[i][j], ".")){
                        currentMicroboard[i][j] = String.valueOf(turn);
                        if (turn == 0){
                            turn=1;
                        }else {
                            turn=0;
                        }
                        String [][] boards = {{".",".","."},{".",".","."},{".",".","."}};
                        boards[i][j] = String.valueOf(-1);
                    }
                }
            }
        }
    }

    private String[][] findMicroBoard(String[][] boards, IGameState state){
        int multi = 3;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (Objects.equals(boards[i][j], AVAILABLE_FIELD)){
                    String[][] boardState = new String[3][3];
                    for (int k = 0;k<3;k++){
                        for (int l = 0; l<3; l++){
                            boardState[k][l] = state.getField().getBoard()[i*multi+k][j*multi+l];
                        }
                    }
                    return boardState;
                }
            }
        }
        return null;
    }
}
