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
        evaluatePosition(0,false,state);
        return new Move(0,0);
    }

    @Override
    public String getBotName() {
        return "PlanBBot";
    }

    private IMove evaluatePosition(int currentDepth, boolean isMaximizer,IGameState state){
        int bestVal = -1000;
        Move bestMove = new Move(0,0);
        String[][] board = state.getField().getBoard();
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                int x = findMicroBoard(state.getField().getMacroboard())[0][0] * 3 + i;
                int y = findMicroBoard(state.getField().getMacroboard())[0][1] * 3 + j;
                if (Objects.equals(state.getField().getBoard()[x][y], ".")){
                    board[x][y]=String.valueOf(botId);
                    String[][] boards = {{".",".","."},{".",".","."},{".",".","."}};
                    boards[i][j] = AVAILABLE_FIELD;
                    int moveval = minimax(board,isMaximizer,currentDepth,boards);
                    board[x][y]=".";
                    if (moveval>bestVal){
                        bestVal = moveval;
                        bestMove = new Move(x,y);
                    }
                }
            }
        }
        return bestMove;
    }

    private int[][] findMicroBoard(String[][] boards){
        int multi = 3;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                if (Objects.equals(boards[i][j], AVAILABLE_FIELD)){
                    return new int[][]{{i,j}};
                }
            }
        }
        return null;
    }

    private int minimax(String[][] board,boolean isMax,int currentDepth, String[][]boards){
        if (currentDepth == depth){
            return 0; // here we will determine the state of the board
        }

        if (isMax){
            int best = -1000;
            for (int i = 0;i<3;i++){
                for (int j=0;j<3;j++){
                    int x = findMicroBoard(boards)[0][0] * 3 + i;
                    int y = findMicroBoard(boards)[0][1] * 3 + j;
                    if (Objects.equals(board[x][y], ".")){
                        board[x][y]=String.valueOf(botId);
                        boards = new String[][]{{".", ".", "."}, {".", ".", "."}, {".", ".", "."}};
                        boards[i][j] = AVAILABLE_FIELD;
                        best = Math.max(best,minimax(board,!isMax,currentDepth+1,boards));
                        board[x][y]=".";
                    }
                }
            }
            return best;
        }else {
            int best = 1000;
            for (int i = 0;i<3;i++){
                for (int j=0;j<3;j++){
                    int x = findMicroBoard(boards)[0][0] * 3 + i;
                    int y = findMicroBoard(boards)[0][1] * 3 + j;
                    if (Objects.equals(board[x][y], ".")){
                        board[x][y]=String.valueOf(opponentId);
                        boards = new String[][]{{".", ".", "."}, {".", ".", "."}, {".", ".", "."}};
                        boards[i][j] = AVAILABLE_FIELD;
                        best = Math.min(best,minimax(board,!isMax,currentDepth+1,boards));
                        board[x][y]=".";
                    }
                }
            }
            return best;
        }
    }
}
