package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dk.easv.bll.field.IField.AVAILABLE_FIELD;

public class PlanBBot implements IBot{
    private int depth = 5;
    private int botId;
    private int opponentId;
    @Override
    public IMove doMove(IGameState state) {

        if (state.getMoveNumber()==0){
            botId =0;
            opponentId=1;
            return new Move(4,4);
        } else if (state.getMoveNumber()==1) {
            botId=1;
            opponentId=0;
        }

        return new Move(0,0);
    }

    @Override
    public String getBotName() {
        return "PlanBBot";
    }

    private IMove evaluatePosition(String[][] currentMicroboard, int currentDepth, boolean isMaximizer,IGameState state){
        int bestVal = -1000;
        Move bestMove = new Move(0,0);
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (Objects.equals(currentMicroboard[i][j], ".")){
                    currentMicroboard[i][j]=String.valueOf(botId);
                    int moveval = minimax(currentMicroboard,false,0,state.getField().getBoard());
                    currentMicroboard[i][j]=".";
                    if (moveval>bestVal){
                        bestVal = moveval;
                        bestMove = new Move(i,j);
                    }
                }
            }
        }
        return bestMove;
    }

    private String[][] findMicroBoard(String[][] boards, String[][] board){
        int multi = 3;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (Objects.equals(boards[i][j], AVAILABLE_FIELD)){
                    String[][] boardState = new String[3][3];
                    for (int k = 0;k<3;k++){
                        for (int l = 0; l<3; l++){
                            boardState[k][l] = board[i*multi+k][j*multi+l];
                        }
                    }
                    return boardState;
                }
            }
        }
        return null;
    }

    private int minimax(String[][] currentMicroboard,boolean isMax,int currentDepth,String[][] board){
        if (currentDepth == depth){
            return 0; // here we will determine the state of the board
        }

        if (isMax){
            int best = -1000;
            for (int i = 0;i<3;i++){
                for (int j=0;j<3;j++){
                    if (Objects.equals(currentMicroboard[i][j], ".")){
                        currentMicroboard[i][j]=String.valueOf(botId);
                        board[i][j]=String.valueOf(botId);
                        String[][] boards = {{".",".","."},{".",".","."},{".",".","."}};
                        boards[i][j] = AVAILABLE_FIELD;
                        best = Math.max(best,minimax(findMicroBoard(boards,board),!isMax,currentDepth+1,board));
                        currentMicroboard[i][j]=".";
                    }
                }
            }
            return best;
        }else {
            int best = 1000;
            for (int i = 0;i<3;i++){
                for (int j=0;j<3;j++){
                    if (Objects.equals(currentMicroboard[i][j], ".")){
                        currentMicroboard[i][j]=String.valueOf(opponentId);
                        board[i][j]=String.valueOf(botId);
                        String[][] boards = {{".",".","."},{".",".","."},{".",".","."}};
                        boards[i][j] = AVAILABLE_FIELD;
                        best = Math.min(best,minimax(findMicroBoard(boards,board),!isMax,currentDepth+1,board));
                        currentMicroboard[i][j]=".";
                    }
                }
            }
            return best;
        }
    }
}
