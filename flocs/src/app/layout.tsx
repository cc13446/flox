import type { Metadata } from "next";
import "./index.css";

export const metadata: Metadata = {
  title: "Flox",
  description: "Welcome to Flox",
};

export default function RootLayout({ children, }: Readonly<{ children: React.ReactNode; }>) {
  return (
    <html lang="en">
      <body className="h-dvh">{children}</body>
    </html>
  );
}
