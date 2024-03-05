package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

public class PlanBBot implements IBot{
    private int depth = 5;
    @Override
    public IMove doMove(IGameState state) {
        System.out.println(state.getField().getAvailableMoves());
        if (state.getMoveNumber()==0){
            return new Move(4,4);
        }
        return new Move(0,0);
    }

    @Override
    public String getBotName() {
        return "PlanBBot";
    }

    private void evaluatePosition(IGameState state){
        for (int i = 0;i<state.getField().getAvailableMoves().size();i++){

        }
    }
}
