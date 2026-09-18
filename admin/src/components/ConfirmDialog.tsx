import { useEffect, useRef } from 'react';
import { useConfirmStore } from '../store/confirmStore.ts';

function ConfirmDialog() {
  const confirmRequest = useConfirmStore((s) => s.confirmRequest);
  const resolveConfirm = useConfirmStore((s) => s.resolveConfirm);
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) {
      return;
    }

    if (confirmRequest && !dialog.open) {
      dialog.showModal();
    }
  }, [confirmRequest]);

  function closeDialog(accepted: boolean) {
    resolveConfirm(accepted);
    dialogRef.current?.close();
  }

  return (
    <dialog
      ref={dialogRef}
      className="confirm-dialog"
      onCancel={(event) => {
        event.preventDefault();
        closeDialog(false);
      }}
    >
      {confirmRequest && (
        <form
          method="dialog"
          className="confirm-dialog-form"
          onSubmit={(event) => {
            event.preventDefault();
            closeDialog(true);
          }}
        >
          <p className="confirm-dialog-message">{confirmRequest.message}</p>
          <div className="confirm-dialog-actions">
            <button type="button" className="btn btn-ghost" onClick={() => closeDialog(false)}>
              Annuler
            </button>
            <button type="submit" className="btn btn-danger" autoFocus>
              Confirmer
            </button>
          </div>
        </form>
      )}
    </dialog>
  );
}

export default ConfirmDialog;
