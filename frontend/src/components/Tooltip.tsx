import React, { ReactNode } from "react";

interface TooltipProps {
	text: ReactNode;
	children: ReactNode;
	position?: "top" | "bottom" | "left" | "right" | "top-left" | "top-right" | "bottom-left" | "bottom-right";
}

export default function Tooltip({ text, children, position = "top" }: TooltipProps) {
	const positionClasses = {
		top: "bottom-full left-1/2 -translate-x-1/2 mb-2",
		bottom: "top-full left-1/2 -translate-x-1/2 mt-2",
		left: "right-full top-1/2 -translate-y-1/2 mr-2",
		right: "left-full top-1/2 -translate-y-1/2 ml-2",
		"top-left": "bottom-full right-0 mb-2",
		"top-right": "bottom-full left-0 mb-2",
		"bottom-left": "top-full right-0 mt-2",
		"bottom-right": "top-full left-0 mt-2",
	};

	const arrowClasses = {
		top: "top-full left-1/2 -translate-x-1/2 border-l-transparent border-r-transparent border-b-transparent border-t-[#1E1E1E]",
		bottom: "bottom-full left-1/2 -translate-x-1/2 border-l-transparent border-r-transparent border-t-transparent border-b-[#1E1E1E]",
		left: "left-full top-1/2 -translate-y-1/2 border-t-transparent border-b-transparent border-r-transparent border-l-[#1E1E1E]",
		right: "right-full top-1/2 -translate-y-1/2 border-t-transparent border-b-transparent border-l-transparent border-r-[#1E1E1E]",
		"top-left":
			"top-full right-2 border-l-transparent border-r-transparent border-b-transparent border-t-[#1E1E1E]",
		"top-right":
			"top-full left-2 border-l-transparent border-r-transparent border-b-transparent border-t-[#1E1E1E]",
		"bottom-left":
			"bottom-full right-2 border-l-transparent border-r-transparent border-t-transparent border-b-[#1E1E1E]",
		"bottom-right":
			"bottom-full left-2 border-l-transparent border-r-transparent border-t-transparent border-b-[#1E1E1E]",
	};

	return (
		<div className="relative group inline-block">
			{children}
			<div
				className={`absolute ${positionClasses[position]} px-2 py-1 bg-[#1E1E1E] text-white text-xs rounded whitespace-nowrap pointer-events-none opacity-0 group-hover:opacity-100 transition-opacity duration-200 z-50`}
			>
				{text}
				<div className={`absolute ${arrowClasses[position]} border-4`} style={{ width: 0, height: 0 }} />
			</div>
		</div>
	);
}
