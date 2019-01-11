package vector_editor.model;

import java.util.ArrayList;

public class Model {


    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }


    private Workspace workspace;
    private ArrayList<Workspace> workspaceHistory;

    public void saveCurrentWorkspaceToHistory() {
        workspaceHistory.add(workspace);
        for (Workspace workspace : workspaceHistory) {
            System.out.println(workspace.getShapes());
        }

    }

    public Model() {
        workspaceHistory = new ArrayList<>();
    }

    public int getPreviousWorkspaceStateId() {
        return workspaceHistory.size() - 1;
    }

    public void setWorkspaceToPreviousState() {
        try {
            workspace = workspaceHistory.get(getPreviousWorkspaceStateId());
            workspaceHistory.remove(getPreviousWorkspaceStateId());

        } catch (Exception e) {
        }
        if (getPreviousWorkspaceStateId() < 0) {
            workspaceHistory.clear();
        }
    }


}
