package tn.iset.m2glnt.client.viewer.presenter.dialog;

/**
 * Résultat d'un formulaire de slot avec contenu générique
 */
public class SlotFormResult<T> {
    private final SlotFormAction action;
    private final T content;

    public SlotFormResult(SlotFormAction action, T content) {
        this.action = action;
        this.content = content;
    }

    public SlotFormAction getAction() {
        return action;
    }

    public T getContent() {
        return content;
    }
}