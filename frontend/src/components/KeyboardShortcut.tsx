import React from "react";

interface KeyboardShortcutProps {
	keys: string[];
}

export default function KeyboardShortcut({ keys }: KeyboardShortcutProps) {
	return (
		<span className="inline-flex items-center gap-0.5">
			{keys.map((key, index) => (
				<React.Fragment key={index}>
					<kbd className="inline-flex items-center justify-center min-w-[18px] h-[18px] px-1 text-[11px] font-sans bg-[#2D2D2D] border border-[#454545] rounded shadow-sm">
						{key}
					</kbd>
				</React.Fragment>
			))}
		</span>
	);
}
