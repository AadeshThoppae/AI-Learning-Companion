"use client";

import { useState, useRef, useEffect, ReactNode } from "react";

interface VerticalSplitterProps {
  topContent: ReactNode;
  bottomContent: ReactNode;
  defaultTopHeight?: number; // percentage (0-100)
  minTopHeight?: number; // percentage
  minBottomHeight?: number; // percentage
  onResize?: () => void;
}

export default function VerticalSplitter({
  topContent,
  bottomContent,
  defaultTopHeight = 60,
  minTopHeight = 10,
  minBottomHeight = 10,
  onResize,
}: VerticalSplitterProps) {
  const [topHeight, setTopHeight] = useState(defaultTopHeight);
  const [isDragging, setIsDragging] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!isDragging) return;

    const handleMouseMove = (e: MouseEvent) => {
      if (!containerRef.current) return;

      const containerRect = containerRef.current.getBoundingClientRect();
      const containerHeight = containerRect.height;
      const mouseY = e.clientY - containerRect.top;

      // Calculate new top height as percentage
      let newTopHeight = (mouseY / containerHeight) * 100;

      // Apply constraints
      newTopHeight = Math.max(minTopHeight, Math.min(100 - minBottomHeight, newTopHeight));

      setTopHeight(newTopHeight);
      onResize?.();
    };

    const handleMouseUp = () => {
      setIsDragging(false);
    };

    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);

    return () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isDragging, minTopHeight, minBottomHeight, onResize]);

  const handleMouseDown = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  return (
    <div ref={containerRef} className="h-full w-full flex flex-col">
      {/* Top panel */}
      <div style={{ height: `${topHeight}%` }} className="overflow-hidden">
        {topContent}
      </div>

      {/* Draggable divider */}
      <div
        onMouseDown={handleMouseDown}
        className={`h-1.5 flex flex-row bg-transparent select-none justify-center relative cursor-ns-resize ${
          isDragging ? "bg-[#1990ff]" : ""
        }`}
      >
        <span className="ml-[-1px] h-[2px] w-5 block bg-[#ffffff24] self-center" />
        <div className="absolute inset-0 hover:bg-[#1990ff] h-0.5 my-0.5" />
      </div>

      {/* Bottom panel */}
      <div style={{ height: `${100 - topHeight}%` }} className="overflow-hidden">
        {bottomContent}
      </div>
    </div>
  );
}
