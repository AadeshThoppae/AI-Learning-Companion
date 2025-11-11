interface LoadingModalProps {
	isOpen: boolean;
	message: string;
}

/**
 * LoadingModal component for displaying a full-page loading overlay
 *
 * Features:
 * - Full-page dark backdrop that prevents interaction
 * - Smooth fade-in/fade-out animation
 * - Centered spinner with customizable message
 * - Semi-transparent overlay (70% opacity)
 *
 * @param isOpen - Controls visibility of the modal
 * @param message - The loading message to display
 * @returns A full-page modal overlay with loading spinner
 */
export default function LoadingModal({ isOpen, message }: LoadingModalProps) {
	if (!isOpen) return null;

	return (
		<div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 transition-opacity duration-300 ease-in-out">
			<div className="flex flex-col items-center gap-4">
				<div className="h-16 w-16 animate-spin rounded-full border-b-2 border-blue-500"></div>
				<span className="text-lg text-gray-300">{message}</span>
			</div>
		</div>
	);
}
