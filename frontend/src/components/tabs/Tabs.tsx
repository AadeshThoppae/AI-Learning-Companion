'use client'

interface TabsProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  tabs: string[];
}

export default function Tabs({ activeTab, setActiveTab, tabs }: TabsProps) {
	return (
        <div className={"flex justify-center mb-8"}>
            <div className={"bg-white dark:bg-gray-800 rounded-lg p-1 shadow-lg"}>
                {tabs.map((tab) => (
                    <button
                        key={tab}
                        onClick={() => setActiveTab(tab)}
                        className={`px-6 py-2 rounded-md font-medium transition-all ${
                            activeTab === tab
                                ? 'bg-blue-500 text-white shadow-md'
                                : 'text-gray-600 dark:text-gray-300 hover:text-blue-500 cursor-pointer'
                        }`}
                    >
                        {tab}
                    </button>
                ))}
            </div>
        </div>
    );
}
