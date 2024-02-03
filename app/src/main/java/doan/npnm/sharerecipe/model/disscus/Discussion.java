package doan.npnm.sharerecipe.model.disscus;

import java.util.ArrayList;

import doan.npnm.sharerecipe.model.BaseModel;
import doan.npnm.sharerecipe.model.disscus.*;

public class Discussion extends BaseModel<Discussion> {
    public DisscusAuth DisscusAuth= new DisscusAuth();

    public String Time= getTimeNow();
    public String Content= "";

    public DiscussIcon DiscussIcon= doan.npnm.sharerecipe.model.disscus.DiscussIcon.NONE;
    public DiscussType DiscussType= doan.npnm.sharerecipe.model.disscus.DiscussType.DISSCUS;

    public ArrayList<Discussion> DiscussionArray= new ArrayList<>();
}
