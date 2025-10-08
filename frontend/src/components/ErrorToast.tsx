import { useEffect, useState } from "react";
import { AiOutlineClose } from "react-icons/ai";

/**
 * Props interface for the ErrorToast component
 */
interface ErrorToastProps {
  message: string;
  onClose: () => void;
}

/**
 * ErrorToast component for displaying temporary error notifications
 * Features smooth slide-in/out animations and auto-dismiss functionality
 * 
 * @param props - The component props containing message and close handler
 * @returns JSX element containing an animated error toast notification
 */
export default function ErrorToast({ message, onClose }: ErrorToastProps) {
  const [visible, setVisible] = useState(false); // Start hidden

  useEffect(() => {
    // Reset to hidden state when message changes
    setVisible(false);

    // Animate in immediately after mount/message change
    const animateIn = setTimeout(() => {
      setVisible(true);
    }, 10); // Small delay to ensure component is mounted

    // Auto-hide after 3 seconds
    const autoHide = setTimeout(() => {
      setVisible(false);
    }, 3000);

    return () => {
      clearTimeout(animateIn);
      clearTimeout(autoHide);
    };
  }, [message]); // Add message as dependency

  // When visibility changes to false, call onClose after animation
  useEffect(() => {
    if (!visible) {
      const timer = setTimeout(onClose, 300); // match slide-out duration
      return () => clearTimeout(timer);
    }
  }, [visible, onClose]);

  return (
    <div
      className={`fixed top-4 right-4 z-50 w-auto max-w-md p-2 bg-red-100 border border-red-400 text-red-700 rounded-lg flex items-center
                  shadow-lg transition-all duration-300 ${
                    visible
                      ? "transform translate-y-0 opacity-100"
                      : "transform -translate-y-12 opacity-0"
                  }`}
    >
      <span className="flex-1">{message}</span>
      <button
        onClick={() => setVisible(false)}
        className="ml-3 text-red-700 hover:text-red-900 hover:scale-110 transition-transform cursor-pointer flex-shrink-0"
      >
        <AiOutlineClose size={20} />
      </button>
    </div>
  );
}