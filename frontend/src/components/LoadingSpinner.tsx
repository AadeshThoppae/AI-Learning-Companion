interface LoadingSpinnerProps {
	message: string;
	size?: 'sm' | 'md' | 'lg';
}

/**
 * LoadingSpinner component for displaying a centered loading indicator
 *
 * @param message - The loading message to display
 * @param size - The size of the spinner (sm: 8x8, md: 12x12, lg: 16x16)
 * @returns A centered loading spinner with message
 */
export default function LoadingSpinner({ message, size = 'md' }: LoadingSpinnerProps) {
	const sizeClasses = {
		sm: 'h-8 w-8',
		md: 'h-12 w-12',
		lg: 'h-16 w-16'
	};

	return (
		<div className="flex items-center justify-center py-12">
			<div className={`animate-spin rounded-full border-b-2 border-blue-500 ${sizeClasses[size]}`}></div>
			<span className="ml-4 text-gray-400">
				{message}
			</span>
		</div>
	);
}
