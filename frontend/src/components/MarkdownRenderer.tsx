'use client'

import { useEffect, useState } from 'react'
import { MDXRemote, MDXRemoteSerializeResult } from 'next-mdx-remote'
import { serialize } from 'next-mdx-remote/serialize'

/**
 * Props interface for the MarkdownRenderer component
 */
interface Props {
  content: string
}

/**
 * MarkdownRenderer component for converting and displaying markdown content as React components
 * Uses next-mdx-remote to serialize markdown content and render it with MDX support
 * 
 * @param props - The component props containing the markdown content
 * @returns JSX element containing the rendered markdown or loading state
 */
export default function MarkdownRenderer({ content }: Props) {
  const [mdxSource, setMdxSource] = useState<MDXRemoteSerializeResult | null>(null)

  /**
   * Effect hook to serialize markdown content when it changes
   * Converts raw markdown string to MDX format for safe rendering
   */
  useEffect(() => {
    /**
     * Asynchronous function to serialize markdown content
     * Handles serialization errors gracefully with console logging
     */
    const serializeContent = async () => {
      try {
        const serialized = await serialize(content)
        setMdxSource(serialized)
      } catch (error) {
        console.error('Error serializing markdown:', error)
      }
    }

    serializeContent()
  }, [content])

  // Show loading state while content is being serialized
  if (!mdxSource) {
    return <div>Loading...</div>
  }

  return <MDXRemote {...mdxSource} />
}