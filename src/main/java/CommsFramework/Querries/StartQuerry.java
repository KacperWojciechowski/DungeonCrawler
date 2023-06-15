package CommsFramework.Querries;

import CommsFramework.Enums.Action;
import CommsFramework.Enums.Status;

public class StartQuerry {
    private final Action action = Action.start;
    private final Status status;

    StartQuerry(Status status)
    {
        this.status = status;
    }
}
