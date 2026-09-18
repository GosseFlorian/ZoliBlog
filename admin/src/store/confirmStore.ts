import { create } from 'zustand';

interface ConfirmRequest {
  message: string;
  resolve: (accepted: boolean) => void;
}

interface ConfirmState {
  confirmRequest: ConfirmRequest | null;
  requestConfirm: (message: string) => Promise<boolean>;
  resolveConfirm: (accepted: boolean) => void;
  reset: () => void;
}

export const useConfirmStore = create<ConfirmState>((set, get) => ({
  confirmRequest: null,

  requestConfirm: (message) =>
    new Promise((resolve) => {
      set({ confirmRequest: { message, resolve } });
    }),

  resolveConfirm: (accepted) => {
    const request = get().confirmRequest;
    if (!request) {
      return;
    }
    request.resolve(accepted);
    set({ confirmRequest: null });
  },

  reset: () => {
    get().confirmRequest?.resolve(false);
    set({ confirmRequest: null });
  },
}));

export function requestConfirm(message: string): Promise<boolean> {
  return useConfirmStore.getState().requestConfirm(message);
}
