package CommsFramework;

public enum Status {
    Error(0), Ok(1);

    private final int ID;

    Status(int ID)
    {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public static Status getByID(int ID)
    {
        if (ID == 1)
        {
            return Ok;
        }
        else
        {
            return Error;
        }
    }
}
