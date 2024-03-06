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
    private int depth = 3;
    private int botId;
    private int opponentId;
    private String[][] bigBoard = new String[3][3];
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
        return evaluatePosition(0,false,state);
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
            int positionValue=0;
            for (int i=0;i<3;i++){
                for (int j=0;j<3;j++){
                    String[][] microBoard = takeMicroboard(board,i,j);
                    positionValue +=evalueateMicroBoard(microBoard,i,j);
                }
            }
            return positionValue;
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

    private String[][] takeMicroboard(String[][] board, int x,int y){
        String[][] boardState = new String[3][3];
        for (int k = 0;k<3;k++){
            for (int l = 0; l<3; l++){
                boardState[k][l] = board[x*3+k][y*3+l];
            }
        }
        return boardState;
    }

    private int evalueateMicroBoard(String [][] microBoard,int x,int y){
        String[] checkTable = new String[3];
        int evaluation =0;
        int sum =0;
        for (int i=0;i<3;i++){ // check cols
            for(int j=0;j<3;j++){
                checkTable[j] = microBoard[i][j];
            }
            evaluation = checkCheckTable(checkTable,x,y);
            if (evaluation == 10 || evaluation==-10){
                return evaluation;
            }
            sum+=evaluation;
        }
        for (int i=0;i<3;i++){ // check rows
            for(int j=0;j<3;j++){
                checkTable[j] = microBoard[j][i];
            }
            evaluation = checkCheckTable(checkTable,x,y);
            if (evaluation == 10 || evaluation==-10){
                return evaluation;
            }
            sum+=evaluation;
        }
        for (int i = 0; i < 3; i++) { // check diagonal one
            checkTable[i] = microBoard[i][i];
        }
        evaluation = checkCheckTable(checkTable,x,y);
        if (evaluation == 10 || evaluation==-10){
            return evaluation;
        }
        sum+=evaluation;

        for (int i = 2; i > -1; i--) { // check diagonal two
            checkTable[i] = microBoard[i][i];
        }
        evaluation = checkCheckTable(checkTable,x,y);
        if (evaluation == 10 || evaluation==-10){
            return evaluation;
        }
        sum+=evaluation;
        return sum;
    }

    private int checkCheckTable(String[] checkTable, int x, int y){
        if (Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && Objects.equals(checkTable[2], String.valueOf(botId))) {
            bigBoard[x][y] = String.valueOf(botId);
            return 10;
        }
        if (Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && Objects.equals(checkTable[2], String.valueOf(opponentId))) {
            bigBoard[x][y] = String.valueOf(opponentId);
            return -10;
        }
        if (Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && !Objects.equals(checkTable[2], String.valueOf(opponentId))){
            return 4;
        }
        if (!Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(botId)) && !Objects.equals(checkTable[2], String.valueOf(botId))){
            return 4;
        }
        if (Objects.equals(checkTable[0], String.valueOf(opponentId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && !Objects.equals(checkTable[2], String.valueOf(botId))){
            return -4;
        }
        if (!Objects.equals(checkTable[0], String.valueOf(botId)) && Objects.equals(checkTable[1], String.valueOf(opponentId)) && !Objects.equals(checkTable[2], String.valueOf(opponentId))){
            return -4;
        }
        return 0;
    }
}
