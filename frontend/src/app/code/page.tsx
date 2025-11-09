"use client";

import { useState, useEffect } from "react";
import { getCodingTopics } from "@/services/codingService";
import { CodingTopic } from "@/types/codingTypes";
import Link from "next/link";
import { IoMdArrowBack } from "react-icons/io";
import { FaCode } from "react-icons/fa6";

export default function CodingTopicsPage() {
	const [topics, setTopics] = useState<CodingTopic[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useEffect(() => {
		const fetchTopics = async () => {
			try {
				setLoading(true);
				const response = await getCodingTopics();
				if (response.data) {
					setTopics(response.data.topics);
				}
			} catch (err) {
				console.error("Failed to fetch coding topics:", err);
				setError(err instanceof Error ? err.message : "Failed to fetch coding topics");
			} finally {
				setLoading(false);
			}
		};

		fetchTopics();
	}, []);

	const getDifficultyColor = (difficulty: string) => {
		switch (difficulty) {
			case "EASY":
				return "bg-green-500/20 text-green-400 border-green-500";
			case "MEDIUM":
				return "bg-yellow-500/20 text-yellow-400 border-yellow-500";
			case "HARD":
				return "bg-red-500/20 text-red-400 border-red-500";
			default:
				return "bg-gray-500/20 text-gray-400 border-gray-500";
		}
	};

	return (
		<div className="min-h-screen bg-[#1a1a1a] p-8">
			<div className="max-w-7xl mx-auto">
				{/* Header */}
				<div className="flex items-center gap-4 mb-8">
					<Link
						href={"/"}
						className="text-gray-400 hover:text-gray-300 transition-colors flex items-center"
					>
						<IoMdArrowBack className="text-2xl" />
					</Link>
					<div>
						<h1 className="text-3xl font-bold text-gray-100 flex items-center gap-3">
							<FaCode className="text-blue-500" />
							Coding Practice Topics
						</h1>
						<p className="text-gray-400 mt-2">
							Select a topic to start practicing coding problems
						</p>
					</div>
				</div>

				{/* Loading State */}
				{loading && (
					<div className="flex justify-center items-center h-64">
						<div className="text-gray-400 text-lg">Loading topics...</div>
					</div>
				)}

				{/* Error State */}
				{error && (
					<div className="bg-red-500/10 border border-red-500 rounded-lg p-6 text-center">
						<p className="text-red-400 text-lg font-semibold mb-2">Error Loading Topics</p>
						<p className="text-gray-400">{error}</p>
						<p className="text-gray-500 mt-4 text-sm">
							Make sure you have uploaded a document first.
						</p>
					</div>
				)}

				{/* Topics Grid */}
				{!loading && !error && topics.length > 0 && (
					<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
						{topics.map((topic) => (
							<Link
								key={topic.id}
								href={`/code/${topic.id}`}
								className="group block"
							>
								<div className="bg-gradient-to-br from-blue-900/20 to-indigo-900/20 border border-gray-700 rounded-xl p-6 h-full hover:scale-[1.02] hover:border-blue-500/50 transition-all shadow-lg hover:shadow-blue-500/20">
									{/* Header with Difficulty Badge */}
									<div className="flex items-start justify-between mb-4">
										<h3 className="text-xl font-bold text-gray-100 group-hover:text-blue-400 transition-colors flex-1">
											{topic.title}
										</h3>
										<span
											className={`px-3 py-1 text-xs font-semibold rounded-full border ${getDifficultyColor(
												topic.difficulty
											)}`}
										>
											{topic.difficulty}
										</span>
									</div>

									{/* Description */}
									<p className="text-gray-400 text-sm mb-4 line-clamp-3">
										{topic.description}
									</p>

									{/* Keywords */}
									<div className="flex flex-wrap gap-2">
										{topic.keywords.map((keyword, index) => (
											<span
												key={index}
												className="px-2 py-1 text-xs bg-gray-800 text-gray-300 rounded border border-gray-700"
											>
												{keyword}
											</span>
										))}
									</div>
								</div>
							</Link>
						))}
					</div>
				)}

				{/* No Topics State */}
				{!loading && !error && topics.length === 0 && (
					<div className="bg-gray-800/50 border border-gray-700 rounded-lg p-8 text-center">
						<p className="text-gray-400 text-lg">No topics available</p>
						<p className="text-gray-500 mt-2 text-sm">
							Upload a document first to generate coding topics.
						</p>
					</div>
				)}
			</div>
		</div>
	);
}
