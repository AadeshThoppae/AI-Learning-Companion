import React from "react";

interface ConfirmationModalProps {
	isOpen: boolean;
	onClose: () => void;
	onConfirm: () => void;
	title: string;
	message: string;
}

export default function ConfirmationModal({ isOpen, onClose, onConfirm, title, message }: ConfirmationModalProps) {
	if (!isOpen) return null;

	const handleConfirm = () => {
		onConfirm();
		onClose();
	};

	return (
		<div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 animate-fadeIn">
			<div className="bg-[#1E1E1E] rounded-lg p-6 max-w-md w-full mx-4 shadow-xl">
				<h2 className="text-xl font-bold text-white mb-4">{title}</h2>
				<p className="text-[#B1B1B1] mb-6 leading-relaxed">{message}</p>
				<div className="flex justify-end gap-3">
					<button
						onClick={onClose}
						className="px-4 py-2 bg-[#2A2A2A] text-white rounded hover:bg-[#333333] transition-colors cursor-pointer"
					>
						Cancel
					</button>
					<button
						onClick={handleConfirm}
						className="px-4 py-2 bg-[#FF6B6B] text-white rounded hover:bg-[#FF5252] transition-colors cursor-pointer"
					>
						Confirm
					</button>
				</div>
			</div>
		</div>
	);
}
